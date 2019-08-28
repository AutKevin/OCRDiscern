package com.autumn.tool;

import org.apache.poi.hssf.OldExcelFormatException;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class ExcelUtils {

    /**
     * 新建Excel文件，New Workbook
     * @param sheetName 新建表单名称,可为null,默认为当前日期
     * @param headList 表头List集合
     * @param dataList 数据List<List<集合>>(行<列>)
     * @param path 新建excel路径
     * @return 是否导出成功
     */
    public static boolean createExcel(String sheetName, List<String> headList, List<List<String>> dataList, String path){
        Workbook wb = null;
        /*创建文件*/
        if (path.endsWith(".xls")) {
            /*操作Excel2003以前（包括2003）的版本，扩展名是.xls */
            wb = new HSSFWorkbook();
        }else if (path.endsWith(".xlsx")){
            /*XSSFWorkbook:是操作Excel2007的版本，扩展名是.xlsx */
            wb = new XSSFWorkbook();
        }
        /*Excel文件创建完毕*/
        CreationHelper createHelper = wb.getCreationHelper();  //创建帮助工具

        /*创建表单*/
        Sheet sheet = wb.createSheet(sheetName!=null?sheetName:"new Sheet");
        // Note that sheet name is Excel must not exceed 31 characters（注意sheet的名字的长度不能超过31个字符，若是超过的话，会自动截取前31个字符）
        // and must not contain any of the any of the following characters:（不能包含下列字符）
        // 0x0000  0x0003  colon (:)  backslash (\)  asterisk (*)  question mark (?)  forward slash (/)  opening square bracket ([)  closing square bracket (])
        /*若是包含的话，会报错。但有一个解决此问题的方法，
        就是调用WorkbookUtil的createSafeSheetName(String nameProposal)方法来创建sheet name,
        若是有如上特殊字符，它会自动用空字符来替换掉，自动过滤。*/
        /*String safeName = WorkbookUtil.createSafeSheetName("[O'Brien's sales*?]"); // returns " O'Brien's sales   "过滤掉上面出现的不合法字符
        Sheet sheet3 = workbook.createSheet(safeName); //然后就创建成功了*/
        /*表单创建完毕*/

        //设置字体
        Font headFont = wb.createFont();
        headFont.setFontHeightInPoints((short)14);
        headFont.setFontName("Courier New");
        headFont.setItalic(false);
        headFont.setStrikeout(false);
        //设置头部单元格样式
        CellStyle headStyle = wb.createCellStyle();
        headStyle.setBorderBottom(BorderStyle.THICK);  //设置单元格线条
        headStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex());   //设置单元格颜色
        headStyle.setBorderLeft(BorderStyle.THICK);
        headStyle.setLeftBorderColor(IndexedColors.BLACK.getIndex());
        headStyle.setBorderRight(BorderStyle.THICK);
        headStyle.setRightBorderColor(IndexedColors.BLACK.getIndex());
        headStyle.setBorderTop(BorderStyle.THICK);
        headStyle.setTopBorderColor(IndexedColors.BLACK.getIndex());
        headStyle.setAlignment(HorizontalAlignment.CENTER);    //设置水平对齐方式
        headStyle.setVerticalAlignment(VerticalAlignment.CENTER);  //设置垂直对齐方式
        //headStyle.setShrinkToFit(true);  //自动伸缩
        headStyle.setFont(headFont);  //设置字体
        /*设置数据单元格格式*/
        CellStyle dataStyle = wb.createCellStyle();
        dataStyle.setBorderBottom(BorderStyle.THIN);  //设置单元格线条
        dataStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex());   //设置单元格颜色
        dataStyle.setBorderLeft(BorderStyle.THIN);
        dataStyle.setLeftBorderColor(IndexedColors.BLACK.getIndex());
        dataStyle.setBorderRight(BorderStyle.THIN);
        dataStyle.setRightBorderColor(IndexedColors.BLACK.getIndex());
        dataStyle.setBorderTop(BorderStyle.THIN);
        dataStyle.setTopBorderColor(IndexedColors.BLACK.getIndex());
        dataStyle.setAlignment(HorizontalAlignment.LEFT);    //设置水平对齐方式
        dataStyle.setVerticalAlignment(VerticalAlignment.CENTER);  //设置垂直对齐方式
        //dataStyle.setShrinkToFit(true);  //自动伸缩
        /*创建行Rows及单元格Cells*/
        Row headRow = sheet.createRow(0); //第一行为头
        for (int i=0;i<headList.size();i++){  //遍历表头数据
            Cell cell = headRow.createCell(i);  //创建单元格
            cell.setCellValue(createHelper.createRichTextString(headList.get(i)));  //设置值
            cell.setCellStyle(headStyle);  //设置样式
        }

        int rowIndex = 1;  //当前行索引
        //创建Rows
        for (List<String> rowdata : dataList){ //遍历所有数据
            Row row = sheet.createRow(rowIndex++); //第一行为头
            for (int j = 0;j< rowdata.size();j++){  //编译每一行
                Cell cell = row.createCell(j);
                cell.setCellStyle(dataStyle);
                cell.setCellValue(createHelper.createRichTextString(rowdata.get(j)));
            }
        }
        /*创建rows和cells完毕*/

        /*设置列自动对齐*/
        for (int i =0;i<headList.size();i++){
            sheet.autoSizeColumn(i);
        }
        File file  = new File(path);  //目标Excel文件
        File parent = new File(file.getParent());  //Excel的父文件夹
        if (!parent.exists()){  //判断父文件夹是否存在
            parent.mkdirs();  //建立父文件夹
        }
        try  (OutputStream fileOut = new FileOutputStream(path)) {    //获取文件流
            wb.write(fileOut);   //将workbook写入文件流
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }


    public static Workbook getWorkBook(String path) throws IOException, InvalidFormatException {
        File file  = new File(path);  //目标Excel文件
        File parent = new File(file.getParent());  //Excel的父文件夹
        if (!parent.exists()){  //判断父文件夹是否存在
            parent.mkdirs();  //建立父文件夹
        }

        Workbook wb = null;
        if(file.exists()){  //如果文件存在
            wb = WorkbookFactory.create(file);  //打开文件
        }else {  //如果目标文件不存在
            if (path.endsWith(".xls")) {
            /*操作Excel2003以前（包括2003）的版本，扩展名是.xls */
                wb = new HSSFWorkbook();
            } else if (path.endsWith(".xlsx")) {
            /*XSSFWorkbook:是操作Excel2007的版本，扩展名是.xlsx */
                wb = new XSSFWorkbook();
            } else {
                new Exception(path + "后缀名错误!");
            }
        }
        /*获取表单总数*/
        int sheetCount = wb.getNumberOfSheets();
        /*获取某一表单总行数*/
        Sheet sheet = wb.createSheet();
        //Sheet sheet = wb.getSheetAt(0);
        System.out.println("表单数:"+sheetCount);
        System.out.println("第一行索引:"+sheet.getFirstRowNum());
        System.out.println("最后一行索引"+sheet.getLastRowNum());
        int rowIndex = sheet.getLastRowNum()+1;
        for (int i=0;i<=3;i++) {
            Row row = sheet.createRow(rowIndex++);
            for (int j = 0; j < 10; j++) {
                Cell cell = row.createCell(j);
                cell.setCellValue("格"+i + j);
            }
        }
        try  (OutputStream fileOut = new FileOutputStream(file)) {    //获取文件流
            fileOut.flush();
            wb.write(fileOut);   //将workbook写入文件流
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return wb;
    }

    //创建一个不存在的excel文件
    private static Workbook createNewWorkbookIfNotExist(String fileName) throws Exception {
        Workbook wb = null;
        if(fileName.endsWith(".xls")) {
            wb = new HSSFWorkbook();
        } else if(fileName.endsWith(".xlsx")) {
            wb = new XSSFWorkbook();
        } else {
            throw new Exception("文件类型错误！既不是.xls也不是.xlsx");
        }

        try{
            OutputStream output = new FileOutputStream(fileName);
            wb.write(output);
        }catch(FileNotFoundException e) {
            System.out.println("文件创建失败，失败原因为：" + e.getMessage());
            throw new FileNotFoundException();
        }
        System.out.println(fileName + "文件创建成功！");

        return wb;
    }

    //创建一个新的或者已存在的Excel文档的Workbook
    public static Workbook createWorkbook(String fileName) throws Exception {
        InputStream input = null;
        Workbook wb = null;

        try{
            input = new FileInputStream(fileName);
            wb = WorkbookFactory.create(input);
            if (!new File(fileName).exists()){  //如果不存在
                wb = createNewWorkbookIfNotExist(fileName);   //创建新的
            }
        } catch(OldExcelFormatException e) {
            System.out.println("文件打开失败，原因：要打开的Excel文件版本过低！");
            throw new OldExcelFormatException("文件版本过低");
        } finally {
            if(input != null) {
                input.close();
            }
        }
        return wb;
    }

    //创建sheet
    public static Sheet createSheet(Workbook wb , String sheetName) {
        Sheet sheet = wb.getSheet(sheetName);

        if(sheet == null) {
            System.out.println("表单" + sheetName + "不存在，试图创建该sheet，请稍后……");
            sheet = wb.createSheet(sheetName);
            System.out.println("名为" + sheetName +"的sheet创建成功！");
        }

        return sheet;
    }

    //创建行row
    public static Row createRow(Sheet sheet , int rowNum) {
        Row row = sheet.getRow(rowNum);

        if(row == null) {
            System.out.println("行号为：" + rowNum + "的行不存在，正试图创建该行，请稍后……");
            row = sheet.createRow(rowNum);
            System.out.println("行号为：" + rowNum + "的行创建成功！");
        }

        return row;
    }

    //创建单元格cell
    public static Cell createCell(Row row , int cellNum) {
        Cell cell = row.getCell(cellNum);

        if(cell == null) {
            System.out.println("该单元格不存在，正在试图创建该单元格，请稍后……");
            cell = row.createCell(cellNum);
            System.out.println("该单元格创建成功！");
        }

        return cell;
    }

    /**
     * 追加到已有excel
     * @param dataList 数据
     * @param name 文件名
     */
    public static void addExcel(List<LinkedHashMap<String,Object>> dataList, String name) throws IOException {
        FileInputStream fileInputStream=new FileInputStream("d://"+name+".xls");  //获取d://test.xls,建立数据的输入通道
        POIFSFileSystem poifsFileSystem=new POIFSFileSystem(fileInputStream);  //使用POI提供的方法得到excel的信息
        HSSFWorkbook Workbook=new HSSFWorkbook(poifsFileSystem);//得到文档对象
        HSSFSheet sheet=Workbook.getSheet(name);  //根据name获取sheet表
        HSSFCellStyle cellStyle = Workbook.createCellStyle();
        /*cellStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN); //下边框
        cellStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);//左边框
        cellStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);//上边框
        cellStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);//右边框
        cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 居中*/
        // HSSFRow row=sheet.getRow(0);  //获取第一行
        System.out.println("最后一行的行号 :"+sheet.getLastRowNum() + 1);  //分别得到最后一行的行号，和第3条记录的最后一个单元格
        //System.out.println("最后一个单元格 :"+row.getLastCellNum());  //分别得到最后一行的行号，和第3条记录的最后一个单元格
        // HSSFRow startRow=sheet.createRow((short)(sheet.getLastRowNum()+1)); // 追加开始行
        // -----------------追加数据-------------------
        int start = sheet.getLastRowNum() + 1; //插入数据开始行
        for (int i = 0; i < dataList.size(); i++){
            HSSFRow startRow = sheet.createRow(i+start);
            AtomicInteger j = new AtomicInteger();
            LinkedHashMap<String ,Object> ltem = dataList.get(i);

        }
        // 输出Excel文件
        FileOutputStream out=new FileOutputStream("d://"+name+".xls");  //向d://test.xls中写数据
        out.flush();
        Workbook.write(out);
        out.close();
    }

    public static void readExcel(String name) throws IOException {
        FileInputStream fileInputStream=new FileInputStream("d:\\"+name+".xls");  //获取d://test.xls,建立数据的输入通道
        POIFSFileSystem poifsFileSystem=new POIFSFileSystem(fileInputStream);  //使用POI提供的方法得到excel的信息
        HSSFWorkbook Workbook=new HSSFWorkbook(poifsFileSystem);//得到文档对象
        HSSFSheet sheet=Workbook.getSheet(name);  //根据name获取sheet表
        HSSFRow row=sheet.getRow(1);  //获取第二行（第一行一般是标题）
        int lastRow = sheet.getLastRowNum(); // 返回的是值从0开始的
        System.out.println("总行数：" + (lastRow + 1));
        int lastCell = row.getLastCellNum(); // 返回的值是从1开始的.....
        System.out.println("总列数：" + lastCell);

        for (int i = 0; i <= lastRow; i++){
            row=sheet.getRow(i);
            if (row != null){
                for (int j = 0; j < lastCell; j++){
                    HSSFCell cell  = row.getCell(j);
                    if (cell != null) System.out.println(cell.getStringCellValue());
                }
            }

        }
    }


    public static void main(String[] args) throws IOException, InvalidFormatException {
        /*List headList = new ArrayList();
        headList.add("SN");
        headList.add("IMEI");
        headList.add("ICCID");

        List<List<String>> dataList = new ArrayList<>();
        List<String> cols = new ArrayList<String>(){};
        cols.add("9121621136308");
        cols.add("867566020192389");
        cols.add("89786619000019855620");
        dataList.add(cols);
        boolean result = createExcel(null,"sheet2",headList,dataList,"D:\\logs\\text.xls");*/

        getWorkBook("D:\\logs\\text2.xls");
    }
}