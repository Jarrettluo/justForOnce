package com.jiaruiblog.justforonce.utils;

import org.springframework.util.StringUtils;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.Charset;

import java.util.*;
import java.util.regex.Pattern;

/**
 * @ClassName CSVToObject
 * @Description 返回内容自定义返回列表或者集合；自定义哪些是不能重复的。
 * 也可以设置为是否转换为map？
 * @Author luojiarui
 * @Date 2022/7/17 1:49 下午
 * @Version 1.0
 **/
public class CSVToObject {

    // 属性和列名的对应关系
    private Map<String, String> fieldRowNameMap = new HashMap<>(8);

    // 属性及属性长度的关系
    private Map<String, Integer> fieldLengthMap = new HashMap<>(8);

    // 属性及属性是否必要的关系
    private Map<String, Boolean> fieldNecessaryMap = new HashMap<>(8);

    private List<String> necessaryField = new ArrayList<>();

    // 属性及属性的正则对应的关系
    private Map<String, String> fieldRegexMap = new HashMap<>(8);

    // 属性及属性的位置关系
    private Map<String, Integer> fieldPosIndexMap = new HashMap<>(8);

    // 属性及属性类型的关系
    // private Map<String, Class<?>> fieldTypeMap = new HashMap<>(8);

    // 属性默认值
    private Map<String, String> fieldDefaultValueMap = new HashMap<>(8);

    // 目标类
    private Class<?> targetClass;

    // 属性不可重复的属性列表
    private List<String> noRepeatField = new ArrayList<>();

    // 忽略正则检查的属性列表
    private List<String> ignoreField = new ArrayList<>();

    // csv 数据的首行个数
    private int rowNum;

    private Class<?> collectionType;

    /**
     * 绑定目标类
     * @author luojiarui
     * @date 2022年7月18日
     * @param targetClass -> Class<?>
     * @return csvToObject
     */
    public CSVToObject bind(Class<?> targetClass) {
        this.targetClass = targetClass;
        // 初始化阶段必需对每个field的类型进行判断基本类型，通过classloader进行判断
        this.config();
        return this;
    }

    /**
     * 增加了自定义返回类型
     * @param targetClass
     * @param collectionType
     * @param <T>
     * @return
     */
    public <T> CSVToObject bind(Class<?> targetClass, Class<T> collectionType) {
        if( !Collection.class.isAssignableFrom(collectionType)) {
            throw new RuntimeException("Collection type is error!");
        }
        this.collectionType = collectionType;
        return bind(targetClass);
    }

    /**
     * @Author luojiarui
     * @Description 对Field的信息进行获取
     * @Date 7:27 下午 2022/7/17
     * @Param []
     * @return void
     **/
    private void config() {
        Field [] fields = targetClass.getDeclaredFields();
        for(int x = 0 ; x < fields.length; x++) {
            Transfer transfer = fields[x].getDeclaredAnnotation(Transfer.class);
            if(transfer != null) {
                this.parseField(fields[x], transfer);
            }
        }
    }

    /**
     * 解析目标类中的属性信息
     * @param field
     * @param transfer
     */
    private void parseField(Field field, Transfer transfer) {

        if(field == null || transfer == null) {
            return;
        }
        String rowName = transfer.rowName();
        if( !StringUtils.hasText(rowName)) {
            return;
        }
        if( !isPrimitive(field.getGenericType().getClass())) {
            return;
        }
        String fieldName = field.getName();
        boolean isRepeat = transfer.isRepeat();

        // if row name is repeated by other rows then return ;
        if(this.fieldRowNameMap.containsValue(rowName)) {
            return;
        }

        this.fieldRowNameMap.put(fieldName, rowName);
        this.fieldNecessaryMap.put(fieldName, transfer.isNecessary());
        this.fieldLengthMap.put(fieldName, transfer.fieldLength());
        this.fieldRegexMap.put(fieldName, transfer.fieldRegex());
        this.fieldDefaultValueMap.put(fieldName, transfer.defaultValue());

        if ( transfer.isNecessary() ) {
            this.necessaryField.add(fieldName);
        }

        if( !isRepeat) {
            this.noRepeatField.add(fieldName);
        }

        if( transfer.ignore()) {
            this.ignoreField.add(fieldName);
        }
    }

    /**
     * 判断是否为Java的基本类型或者包装类
     * @param aClass 判断是否为基本类型
     * @return
     */
    private boolean isPrimitive(Class<?> aClass) {
        return aClass != null && aClass.getClassLoader() == null;
    }

    /**
     * 根据文件路径解析csv文件
     * @param filePath
     * @param <T>
     * @return
     * @throws FileNotFoundException
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     */
    public <T> List<T> csvFileExport(String filePath) throws FileNotFoundException,
            IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        // Step.1 参数检查
        if( !new File(filePath).exists()) {
            throw new FileNotFoundException();
        }
        return csvFileExport(new FileInputStream(filePath));
    }

    /**
     * 导出csv为对象列表
     * @param fileStream
     * @param <T>
     * @return
     * @throws FileNotFoundException
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     */
    public <T> List<T> csvFileExport(InputStream fileStream) throws FileNotFoundException,
            IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        List<T> targets = new ArrayList<>();

        // Step.2 逐行读取csv文件出来
        List<String> caseStringList = null;
        try (InputStreamReader isr = new InputStreamReader(fileStream,
            Charset.forName("gb2312"));
            BufferedReader br = new BufferedReader(isr)) {
            caseStringList = getCSVContentList(br);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(caseStringList == null || caseStringList.isEmpty()) {
            return targets;
        }
        // STEP.3 将读到的列表进行重整
        List<List<String>> midSigList = new ArrayList<>();

        // 传入的csv进行判断是否加首列数据
        int titleLength = this.rowNum;
        if(titleLength < 1) {
            throw new RuntimeException("error!");
        }
        // 判断长度是否足够
        int rows = caseStringList.size() / titleLength;
        for(int i = 0; i < rows; i++) {
            List<String> midSigRows = caseStringList.subList(i * titleLength, (i+1) * titleLength);
            midSigList.add(midSigRows);
        }

        // STEP.4 是否进行正则判断
        midSigList = batchIsMatched(midSigList, this.fieldPosIndexMap);

        if(midSigList.isEmpty()) {
            throw new RuntimeException("midSigList is empty");
        }

        // STEP.5 Object instance
        targets = transformTestCases(midSigList, this.fieldPosIndexMap);
        // STEP.6 清空
        caseStringList.clear();
        // STEP.7 存库
        return targets;
    }

    /**
     * 从文件流中获取字符串列表回来
     * @param br 文件流
     * @return -> List<String>
     * @throws IOException 文件没找到的报错
     */
    private List<String> getCSVContentList(BufferedReader br) throws IOException {
        List<String> titleCsv = CsvFileParser.parseFirstLine(br);

        if(titleCsv == null || titleCsv.isEmpty()) {
            return Collections.emptyList();
        }

        this.rowNum = titleCsv.size();
        // 开始预解析， 仅仅解析头部信息
        parserTestCaseTitle(titleCsv);

        // 如果头部中都不包含必须的，则直接返回空的列表
        if (!this.necessaryField.stream().allMatch(item -> this.fieldNecessaryMap.containsKey(item))) {
            return new ArrayList<>();
        }
        return CsvFileParser.parseCsvLines(br);
    }

    /**
     * 转换csv的每列的头，找到头部的位置信息
     * @param title csv文件的头部信息列表
     */
    private void parserTestCaseTitle( List<String> title) {
        List<String> titleInfo = new ArrayList<>();
        List<String> fieldList = new ArrayList<>();
        for(Map.Entry<String, String> entry : this.fieldRowNameMap.entrySet()) {
            fieldList.add(entry.getKey());
            titleInfo.add(entry.getValue());
        }

        for ( int index = 0 ; index < title.size() ; index ++ ) {
            int i = titleInfo.indexOf(title.get(index));
            if( i > -1 ) {
               this.fieldPosIndexMap.put(fieldList.get(i), index);
            }
        }
    }


    /**
     * @Author luojiarui
     * @Description 通过对csv的每一行进行正则匹配，筛选出符合要求的数据
     * @Date 2:34 下午 2022/7/17
     * @Param [list, titleMap]
     * @return java.util.List<java.util.List<java.lang.String>>
     **/
    private List<List<String>> batchIsMatched(List<List<String>> list, Map<String, Integer> posIndex) {
        // 不在标题内的数据应该被删除掉
        List<List<String>> stringList = new ArrayList<>(list);
        Iterator<List<String>> iterator = stringList.iterator();
        while (iterator.hasNext()) {
            List<String> caseLine = iterator.next();
            for (Map.Entry<String, Integer> entry : posIndex.entrySet()) {
                String caseContent;
                try {
                    caseContent = caseLine.get(entry.getValue());
                } catch (IndexOutOfBoundsException e) {
                    iterator.remove();
                    break;
                }

                String titleKey = entry.getKey();
                Integer fieldLength = this.fieldLengthMap.get(titleKey);
                String regex = this.fieldRegexMap.get(titleKey);
                // 如果检查结果等于1，则删除这一行
                if( checkCaseContent(caseContent, titleKey, fieldLength, regex).equals(1)) {
                    iterator.remove();
                }
            }
        }

        return stringList;
    }

    /**
     * @Author luojiarui
     * @Description //检查，没有进行正则匹配
     * @Date 11:16 下午 2022/7/17
     * @Param [caseContent, keyValue, fieldLength]
     * @return java.lang.Integer
     **/
    private Integer checkCaseContent(String caseContent, String keyValue, Integer fieldLength, String currentReg) {
        if ( StringUtils.hasText(caseContent)) {
            // 如果字符超长，直接删除掉
            if(caseContent.length() > fieldLength + 1) {
                return 1;
            }
            // 如果不做限制的字段则直接返回，如果需要做正则判断，则在这里进行
            if( this.ignoreField.contains(keyValue)) {
                return 2;
            }
            // 如果不满足正则，则删除掉这一条
            try {
                if ( !Pattern.matches(currentReg, caseContent)) {
                    return 1;
                }
            } catch (Exception e) {
                return 1;
            }
        }
        return 0;
    }

    /**
     * 将文本信息返回给后端
     * @param cases
     * @param titleMap
     * @param <T>
     * @return
     */
    private <T> List<T> transformTestCases(List<List<String>> cases, Map<String, Integer> titleMap)
            throws InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        List<T> objects = new ArrayList<>();
        for (List<String> aCase : cases) {
            T object = (T) testCaseInstance(titleMap, aCase);
            // 判断每个中间信号的属性是否是完整的，否则就跳过
            if(!getFieldValueByName(this.necessaryField, object)) {
                break;
            }
            objects.add(object);
        }
        return objects;
    }

    /**
     * 将某一行的数据转换成一个对象，
     * @param titleMap -> 映射的row位置
     * @param caseLine -> 字符串列表
     * @return 返回转换后的对象
     */
    private <T> T testCaseInstance(Map<String, Integer> titleMap, List<String> caseLine) throws IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {

        T t = (T) this.targetClass.getDeclaredConstructor().newInstance();

        for (Map.Entry<String, Integer> entry : titleMap.entrySet()) {

            // 找到需要赋值的内容
            String setValue = caseLine.get(entry.getValue());
            // 如果是某某值，则自动设置为某某值
            // 设置不同的类型
            try {
                Method method = this.targetClass.getDeclaredMethod("set" +
                        convertInitialUpper(entry.getKey()), String.class);
                method.invoke(t, setValue);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return t;
    }

    /**
     *
     * @param fileNames
     * @param o
     * @return
     */
    private <T> boolean getFieldValueByName(List<String> fileNames, T o) {
        boolean flag = false;

        for (String fieldName : fileNames) {
            String getter = "get" + convertInitialUpper(fieldName);
            try {
                Method getMethod = this.targetClass.getDeclaredMethod(getter);

                String value = (String) getMethod.invoke(o);
                if( StringUtils.hasText(value)) {
                    flag = true;
                } else {
                    return flag;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        return flag;
    }

    /**
     * @Author luojiarui
     * @Description 首字母转大写
     * @Date 2:47 下午 2022/7/17
     * @Param [value]
     * @return java.lang.String
     **/
    private static String convertInitialUpper(String word) {
        char[] chars = word.toCharArray();
        chars[0] -= 32;
        return new String(chars);
    }
}