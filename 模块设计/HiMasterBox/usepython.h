#include <stdio.h>  
#include "Python.h"
#include <stdlib.h>  
int PyCall( const char * module, const char * function, const char *format, ... )  
{ 
 PyObject* pMod    = NULL;  
 PyObject* pFunc   = NULL;  
 PyObject* pParm   = NULL;  
 PyObject* pRetVal = NULL;  
 //py脚本语句执行PATH为程序根目录
// PyRun_SimpleString("import gevent.monkey; gevent.monkey.patch_thread()");
 PyRun_SimpleString("import sys");
 PyRun_SimpleString("sys.path.append('./')");
 //导入模块  
 if( !(pMod = PyImport_ImportModule(module) ) ){  
  return -1;  
 }  
 //查找函数  
 if( !(pFunc = PyObject_GetAttrString(pMod, function) ) ){  
  return -2;  
 }  
  
 //创建参数  
 va_list vargs;  
 va_start( vargs, format );  
 pParm = Py_VaBuildValue( format, vargs );  
 va_end(vargs);  
  
 //函数调用  
 pRetVal = PyEval_CallObject( pFunc, pParm);  
   
 int ret;  
 PyArg_Parse( pRetVal, "i", &ret );  
 return ret;  
}  
