public class Output
{


public static List<String> getSpecificColumnForDownstreamxml(String fileName,String columnName){
        Document doc=ResourceReaderUtil.readxmlFile(fileName);
        try{
        NodeList emmlList=doc.getElementsByTagName("emm1");
        List<String> recordData=new ArrayList<>();
        for(int i=0;i<emmlList.getLength();i++){
        Node emmlNode=emmlList.item(i);
        if(emmlNode.getNodeType()==Node.ELEMENT_NODE){
        Element emmlElement=(Element)emmlNode;
        Node item=emmlElement.getElement sByTagName(columnName).item(0);
        String itemvalue=(item!=null)?item.getTextContent():";
        recordData.add(itemvalue);
        }
        }
        return recordData;
        }catch(Exception e){
//        // e.printStackTrace();
        }
        return null;
        t
public static List<String> getSpecificColumnForDownstreamxml(String fileName,String columnName){
        Document doc=ResourceReaderUtil.readxmlFile(fileName);
        try{
        NodeList emmlList=doc.getElementsByTagName("emm1");
        List<String> recordData=new ArrayList<>();
        for(int i=0;i<emmlList.getLength();i++){
        Node emmlNode=emmlList.item(i);
        if(emmlNode.getNodeType()==Node.ELEMENT_NODE){
        Element emmlElement=(Element)emmlNode;
        Node item=emmlElement.getElement sByTagName(columnName).item(0);
        String itemvalue=(item!=null)?item.getTextContent():";
        recordData.add(itemvalue);
        }
        }
        return recordData;
        }catch(Exception e){
        // e.printStackTrace();
        }
        return null;
        }
public static int getRecordCount(String fileName,String tagName){
        Document document=ResourceReaderUtil.readxmlFile(fileName);
        NodeList nodeList=document.getElementsByTagName(tagName);
        return nodeList.getLength();
        +
public static String getElementValue(Element parentElement,String tagName){
        NodeList nodeList=parentElement.getElement sByTagName(tagName);
        if(nodeList.getLength()>0){
        Element element=(Element)nodeList.item(0);
        return(element!=null)?element.getTextContent():"";
        }
        return"";
        1
@When("I compare all records of feed files with db")
public void compareAllRecordsOfFeedAndDB(DataTable dataTable){
        List<Map<String, String>>data=dataTable.asMaps(String.class,String.class);
        for(Map<String, String> column:data){
        String fileName=column.get("fileName");
        String tableName=column.get("tableName");
        String fileFormat=column.get("fileFormat");
        String feedSource=column.get("feedSource");
        Map<List<String>,List<String>>allRecordsInView=OracleDBCommonUtils.
        getAllRecordsByQueryAsMap("Select * from "+tableName);
        log.info("Total number of records in view table are : "+allRecordsInView.size());
        Map<List<String>,List<String>>allRecordsFromFeed=TextFileOperationUtil.
        getRecordFromFileAsMap(fileName,
        feedSource);
        log.info("Total number of records in file are :"+allRecordsFromFeed.size());
        Map<List<String>,List<String>>result=E2ECompareUtil.
        compareTwoMap(allRecordsFromFeed,allRecordsInView);
        Assert.assertEquals(result.size(),0,
        |"These records are not as per expectation "+result.toString());
        }
        }
public static Map<List<String>,List<String>>getAllRecordsByQueryAsMap(String query){
        Map<List<String>,List<String>>map=new HashMap<>();
        Connection conn=DBConfig.getoOracleConnection();
        Statement statement=null;
        ResultSet resultSet=null;
        try{
        statement=conn.createStatement();
        statement.setFetchSize(1000);
        resultSet=statement.executeQuery(query);|
        ResultSetMetaData metaData=resultSet.getMetaData();
        int columnCount=metaData.getColumnCount();
        while(resultSet.next()){
        List<String> currentRecordList=new ArrayList<>();
        for(int i=1;i<=columncount;i++){
// System. out.print1n(metaData.getColumnName (i) );
        if(resultSet.getString(i)==null)
        currentRecordList.add("");
        else if(resultSet.getString(i).contains("00:00:00")){
        SimpleDateFormat in=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); //This format in db
        SimpleDateFormat out=new SimpleDateFormat("dd-MMM-yy"); //But in file we required this format
        Date date=in.parse(resultSet.getString(i));
        currentRecordList.add(out.format(date).toUpperCase(Locale.ROOT));
        }else
        currentRecordList.add(resultSet.getString(i));
        }
        map.put(currentRecordList,currentRecordList);
        }
        return map;
        }catch(Exception e){
        // e.printStackTrace();
        }finally{
        DBConfig.closeConnection(conn,statement,resultSet);
        }
        return null;
        }
public static Map<List<string>,List<string>>getRecordFromFileAsMap(String fileName,String feedSource){
        BufferedReader bufferReader=ResourceReaderUtil.getBufferReaderStream(fileName,feedSource);
        Map<List<String>,List<String>>allRecord=new HashMap<>();
        String line;
        int firstLine=1;
        try{
        while((line=bufferReader.readLine())!=null){
        if(feedsource.equals(Constants.UPSTREAM)){
        if((line.startswith("#"))||(firstLine)==1){
        firstLinet+;
        }else{
        String[]value=line.split(getSeparator(fileName));
        allRecord.put(Arrays.asList(value),Arrays.asList(value));
        }
        }
        else
        {String[]value=line.split(getSeparator(fileName),-1);
        allRecord.put(Arrays.asList(value),Arrays.asList(value));
        }
        }
        }catch(IOException e){
        // e.printStackTrace();
        }
        return allRecord;
        t
@e2e User _Account_Relationship File Import_ECoI
        Scenario:Sum value check for a specific column in feed file
        When Verify sum value of a specific column in feed file
        |fileName|tableName|fileFormat|feedSource|columnindex|columnName|
        |UserAccountRelationshipECOI_full|sca_user_account_relationship_ecoi_full_v|txt|starfeed|1|pckey I
        I UserAccountRelationshipECOI_delta I SCA_USER_ACCOUNT_RELATIONSHIP_ECOI_DELTA_T|txt|starfeed 12|PARENTUSERACCCUSTKEY I
@When("Verify sum value of a specific column in feed file")
public void sumValueOfSpecificColumn(DataTable dataTable){
        List<Map<String, String>>data=dataTable.asMaps(String.class,String.class);
        for(Map<String, String> column:data){
        String fileName=column.get("fileName");
        String tableName=column.get("tableName");
        String fileFormat=column.get("fileFormat");
        String feedSource=column.get("feedSource");
        String columnindex=column.get("columnIndex");
        String columnName=column.get("columnName");
        List<String> fileData=TextFileOperationUtil.getSpecificColData(fileName,
        feedSource,Integer.parseInt(columniIndex));
        List<Integer> colDataForSumInFile=fileData.stream().map(Integer: :parseInt).collect(Collectors.toList());
        int sumValueInFile=colDataForSumInFile.stream().mapToInt(Integer: :intValue).sum();
        List<String> dbData=OracleDBCommonUtils.getSpecificColumnForAllRecords(tableName,columnName);
        List<Integer> colDataForSumInDB=dbData.stream().map(Integer: :parseInt).collect(Collectors.toList());
        int sumValueInDB=colDataForSumInDB.stream().mapToInt(Integer: :intValue).sum();
        log.info("The sum value is "+sumValueInFile+" from "+columnName+" column in file");
        log.info("The sum value is "+sumValueInDB+" from "+columnName+" column in DB");
        Assert.assertEquals(sumValueInFile,sumValueInDB);
        }
        |
public static List<string> getSpecificColData(String fileName,String feedSource,int indexofColumn){
        BufferedReader bufferedReader=ResourceReaderUtil.getBufferReaderStream(fileName,feedSource);
        List<String> allRecord=new ArrayList<>();
        String line;
        try{
        while((line=bufferedReader.readLine())!=null){
        String[]value=line.split(getSeparator(fileName),-1);
        allRecord.add(value[indexOfColumn]);
        }
        }catch(IOException e){
        // e.printStackTrace();
        }
        return allRecord;
        +
public static BufferedReader getBufferReaderStream(String fileName,String feedSource){
        String filePath;
        if(feedSource.equals(Constants.STARFEED)){
        filePath=fileName.contains("full")2"src/test/resources/feedfiles/starfeeds/full":
        "src/test/resources/feedfiles/starfeeds/delta";
        }
        else if(feedSource.equals(Constants.DOWNSTREAM)){
        filePath="src/test/resources/feedfiles/downstream/";
        yelse{
        filePath="src/test/resources/feedfiles/upstream/";
        }
        File directory=new File(filePath);
        File[]files=directory.listFiles((dir,name)->name.startsWith(fileName.split("_")[0]));
        System.out.printin(files[0]);
        BufferedReader bufferedReader=null;
        try{
        bufferedReader=new BufferedReader(new FileReader(files[0]));
        }catch(FileNotFoundException e){
        // e.printStackTrace();
        }
        return bufferedReader;
        1
