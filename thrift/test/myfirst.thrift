// namespaces are used in resulting packages
namespace * myfirst

const double PI = 3.1415926

struct MyStruct {
    1: bool mybool,
    2: byte mybyte,
    3: i16 myi16,
    4: i32 myi32,
    5: i64 myi64,
    6: double mydouble,
    7: string mystring,
    8: list<i32> mylist,
    9: set<i32> myset,
    10: map<i32,i32> mymap
}

union MyUnion {
    1: bool mybool,
    2: string mystring
}

enum MyEnum {
    ENUM1,
    ENUM2,
    ENUM3
}

exception MyError {
    1: int error_code,
    2: string error_description
}

typedef i32 int

// here starts the service definition
service MyFirstService {

    // log method - it saves timestamp to given file
    oneway void log(1:string filename),

    // multiply method - returns result of multiplication of two integers; it returns integer as well
    int multiply(1:int number1, 2:int number2),

    // get_log_size method -  returns size of log file; throws exception when problems occur
    int get_log_size(1:string filename) throws (1:MyError error),

}
