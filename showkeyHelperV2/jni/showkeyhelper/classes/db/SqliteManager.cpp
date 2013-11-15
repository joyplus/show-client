#include "SqliteManager.h"

//创建数据库
void SqliteManager:: createSqliteDatebase()
{
    pdb =NULL;
// 1.指定数据库路径
    string path=CCFileUtils::sharedFileUtils()->getWritablePath()+"audio.db3";
//  2.检查指定路径下数据库 audio.db3是否存在
    bool isExist =CCFileUtils::sharedFileUtils()->isFileExist(path);
    if (!isExist) {
         CCLog("%s不存在",path.c_str());
    }
    else
    {
       CCLog("%s存在",path.c_str());
//        在IOS或安卓上删除该数据库
//        your code .........
    }
    result=sqlite3_open(path.c_str(),&pdb);
    if(result==SQLITE_OK)
    {
       CCLog("open database success,  number=%d",result);
    }
    else
    {
      CCLog("open database failed,  number=%d",result);
    }

    this->createTable();
}
//  创建表
bool SqliteManager::createTable()
{


    //创建表
    result=sqlite3_exec(pdb,"create table audio(ID integer primary key autoincrement,audioname text)",NULL,NULL,NULL);
    if(result==SQLITE_OK)
    {
        CCLog("create table success!");
        return true;

    }
    else
    {
        CCLog("create table failed!");
        return false;
    }
}
//    插入一条数据
bool SqliteManager::insertData(const char* index,const char *name)
{
    string s1("insert into audio values(");
    string s2(index);
    string s3(",");
    string s4 (name);

    sql=s1+s2+s3+"'"+s4+"')";
    CCLOG("%s",sql.c_str());
    result=sqlite3_exec(pdb,sql.c_str(),NULL,NULL,NULL);
    if(result!=SQLITE_OK)
    {
         CCLog("insert data failed!");
         return false;
    }
    else
    {
      CCLog("insert data success!");
       return true;
    }

}
//    更新一条数据
bool SqliteManager::updateData(const char* index, const char *name)
{
    string s1("update audio set audioname=");
    string s2(name);
    string s3(" where ID=");
    string s4 (index);
    sql=s1+"'"+s2+"'"+s3+s4;
    CCLOG("%s",sql.c_str());
    result=sqlite3_exec(pdb,sql.c_str(), NULL,NULL,NULL);
    if(result!=SQLITE_OK)
    {
    	CCLog("更新 Id=%s数据失败!",index);
        return false;

    }
    else
    {

    	CCLog("更新 Id=%s数据成功!",index);
        return true;

    }

}
//   删除一条数据
bool SqliteManager::deleteData(const char* index)
{


    string s1("delete from audio where ID=");
    string s2(index);
    sql=s1+s2;
    CCLOG("%s",sql.c_str());
    result=sqlite3_exec(pdb,sql.c_str(), NULL,NULL,NULL);
    if(result!=SQLITE_OK)
    {
         CCLog("delete data failed!");
         return false;
    }
    else
    {

        CCLog("delete data success!");
        return true;
    }

}
//  查询获取数据
const char * SqliteManager::getdata(int index)
{
    int r,c;
    char buff[200]={0};
    string s1("select audioname from audio where ID=");
    sprintf(buff, "%d",index);
    string s2(buff);
    sql=s1+s2;
    CCLOG("%s",sql.c_str());
    result=sqlite3_get_table(pdb,sql.c_str(),&re,&r,&c, NULL);
    CCLog("row is %d,column is %d",r,c);
    if(result!=SQLITE_OK)
    {
        CCLog(" data failed!");
         return " ";
    }
    else
    {

        CCLog("data success!");
        return re[1];
    }

}
void SqliteManager::CloseDataBase()
{
    sqlite3_free_table(re);

    sqlite3_close(pdb);
}

SqliteManager::SqliteManager()
{

}
SqliteManager::~SqliteManager()
{
    if (re!=NULL)
    {
        sqlite3_free_table(re);

        sqlite3_close(pdb);
    }

}
