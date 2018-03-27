#CMake版本信息
cmake_minimum_required(VERSION 3.4.1)

# 支持-std=gnu++11
set(CMAKE_VERBOSE_MAKEFILE on)
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=gnu++11")

# 工程路径
set(pathToProject D:/Android/workspace/OpenCV_image_comparator)
# OpenCV-android-sdk路径
set(pathToOpenCv D:/Android/workspace/OpenCV-android-sdk)

# 配置加载native依赖
include_directories(${pathToOpenCv}/sdk/native/jni/include)

# 添加待编译的cpp文件
add_library(opencv SHARED ${pathToProject}/app/src/main/cpp/opencv.cpp)

# 动态方式加载
add_library(lib_opencv SHARED IMPORTED)
# 引入libopencv_java3.so文件
set_target_properties(lib_opencv PROPERTIES IMPORTED_LOCATION ${pathToProject}/app/src/main/jniLibs/${ANDROID_ABI}/libopencv_java3.so)

target_link_libraries(opencv lib_opencv log)