#include "sqlite3.h"
#include "cocos2d.h"

using namespace cocos2d;
using namespace std;
class SqliteManager
{
private:
 sqlite3 * pdb;
 string sql;
 int result;
 char **re;
public:

    SqliteManager();
    ~SqliteManager();
//   创建一个数据库
    void createSqliteDatebase();
//  创建表
    bool createTable();
//   插入一条数据
    bool insertData(const char* index,const char *name);
//   更新一条数据
    bool updateData(const char* index,const char *name);
//   删除一条数据
    bool deleteData(const char* index);
//  查询获取数据
    const char * getdata(int index);
//  关闭数据库
    void CloseDataBase();

};
