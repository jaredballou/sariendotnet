
/**
 * This native application will load the Java Virtual Machine and setup
 * the ClassPath, and run the Adventure Game Interpreter. It also serve
 * has a container for the Native Win32 Icons. (Which may be use by creating
 * native shortcut to the interpreter.)
 *
 * @author  Dr. Z
 * @version 0.00.00.01
 */

/** Standard Headers */
#include <stdlib.h>
#include <stdio.h>
#include <tchar.h>

/** Windows Headers */
#include <windows.h>
#include "resource.h"

/** Java Native Interface Header */
#include <jni.h>

class CStackAlloc
{
protected:
	unsigned int bufferCount;
	void**       buffers;

public:
	CStackAlloc();
	~CStackAlloc();

public:
	void* alloc(unsigned int size);
	void* realloc(void* data, unsigned int size);

public:
	char* loadString(unsigned int stringID);
};

CStackAlloc::CStackAlloc()
{
	bufferCount = 0;
	buffers     = NULL;
}

CStackAlloc::~CStackAlloc()
{
	unsigned int i;

	if (buffers != NULL)
	{
		for (i = 0; i < bufferCount; i++)
		{
			free(buffers[i]);
		}

		free(buffers);
	}
}

void* CStackAlloc::alloc(unsigned int size)
{
	void* data;

	if (buffers == NULL)
	{
		buffers = (void**)malloc(sizeof(void*));
	}
	else
	{
		buffers = (void**)::realloc(buffers, sizeof(void*) * (bufferCount + 1));
	}

	data = malloc(size);

	buffers[bufferCount] = data;
	bufferCount++;

	return data;
}

void* CStackAlloc::realloc(void* data, unsigned int size)
{
	unsigned int i;

	if (buffers == NULL)
	{
		return alloc(size);
	}

	for (i = 0; i < bufferCount; i++)
	{
		if (buffers[i] == data)
		{
			buffers[i] = realloc(data, size);
			return buffers[i];
		}
	}

	return alloc(size);
}

char* CStackAlloc::loadString(unsigned int stringID)
{
	char* string = (char*)alloc(512);

	LoadString(NULL, stringID, string, 512);
	return string;
}

class CJavaVM
{
protected:
	/** Version Related */
	double   minimalVersion;
	double   selectedVersion;

	/** Library Related */
	HKEY     key;
	HMODULE  module;

	/** Java VM Related */
	JavaVM*  javaVM;
	JNIEnv*  javaEnv;
	jint     errorCode;

	/** Java VM Options */
	JavaVMOption* options;
	jint          optionCount;

public:
	CJavaVM(double minimalVersion = 1.2);
	~CJavaVM();

protected:
	bool    isAllowed(LPSTR version);
	HKEY    obtainJavaKey();
	HMODULE obtainJavaModule(HKEY key);

public:
	bool addClassPath(char* classpath);
	bool addOption   (char* option, void* info = NULL);

	bool createVM();

public:
	JNIEnv* getJavaEnv();
	HKEY    getJavaKey();
	HMODULE getJavaModule();
	JavaVM* getJavaVM();
};

CJavaVM::CJavaVM(double minimalVersion)
{
	this->minimalVersion  = minimalVersion;
	this->selectedVersion = 0.0;

	javaVM    = NULL;
	javaEnv   = NULL;
	errorCode = 0;

	options     = NULL;
	optionCount = 0;

	key    = obtainJavaKey();
	module = obtainJavaModule(key);
}

CJavaVM::~CJavaVM()
{
	int i;

	if (javaVM != NULL)
	{
		javaVM->DestroyJavaVM();
	}

	if (options != NULL)
	{
		for (i = 0; i < optionCount; i++)
		{
			free(options[i].optionString);
		}

		free(options);
	}
}

JNIEnv* CJavaVM::getJavaEnv()
{
	return javaEnv;
}

HKEY CJavaVM::getJavaKey()
{
	return key;
}

HMODULE CJavaVM::getJavaModule()
{
	return module;
}

JavaVM* CJavaVM::getJavaVM()
{
	return javaVM;
}

bool CJavaVM::isAllowed(LPSTR version)
{
	selectedVersion = atof(version);

	return (selectedVersion >= minimalVersion);
}

HKEY CJavaVM::obtainJavaKey()
{
	HKEY  hJavaRE = NULL;
	HKEY  hJavaVM = NULL;
	DWORD type;
	DWORD size;
	char  version[16];

	if (RegOpenKeyEx(
			HKEY_LOCAL_MACHINE,
			"SOFTWARE\\JavaSoft\\Java Runtime Environment",
			0,
			KEY_READ,
			&hJavaRE) != ERROR_SUCCESS)
	{
		return NULL;
	}

	type = REG_SZ;
	size = sizeof(version);
	RegQueryValueEx(hJavaRE, "CurrentVersion", NULL, &type, (LPBYTE)version, &size);

	if (!isAllowed(version))
	{
		DWORD index = 0;

		/* Default Version is not supported...
		 * So we need to look at every VM installed to find one that ~may~ do
		 * the job. */

		while (true)
		{
			size = sizeof(version);
			if (RegEnumKeyEx(hJavaRE, index, version, &size, NULL, NULL, NULL, NULL) != ERROR_SUCCESS)
			{
				break;
			}

			if (isAllowed(version))
			{
				break;
			}

			index++;
		}
	}

	if (selectedVersion >= minimalVersion)
	{
		RegOpenKeyEx(
			hJavaRE,
			version,
			0,
			KEY_READ,
			&hJavaVM);
	}

	RegCloseKey(hJavaRE);
	return hJavaVM;
}

HMODULE CJavaVM::obtainJavaModule(HKEY key)
{
	HMODULE module = NULL;
	DWORD   size   = MAX_PATH * 2;

	TCHAR   lib[MAX_PATH*2];
	DWORD   type;

	RegQueryValueEx(key, TEXT("RuntimeLib"), NULL, &type, (LPBYTE)lib, &size);
	module = LoadLibrary(lib);
	RegCloseKey(key);

	return module;
}

typedef jint (JNICALL *JavaCreateVM)(JavaVM **pvm, JNIEnv **penv, JavaVMInitArgs *args);

bool CJavaVM::addClassPath(char* classPath)
{
	CStackAlloc temp;
	char*       newClassPath;

	newClassPath = (char*)temp.alloc(strlen(classPath) + 20);
	strcpy(newClassPath, "-Djava.class.path=");
	strcat(newClassPath, classPath);

	return addOption(newClassPath);
}

bool CJavaVM::addOption(char* option, void* info)
{
	if (options == NULL)
	{
		options = (JavaVMOption*)malloc(sizeof(JavaVMOption));
	}
	else
	{
		options = (JavaVMOption*)realloc(options, (optionCount + 1) * sizeof(JavaVMOption));
	}

	options[optionCount].optionString = strdup(option);
	options[optionCount].extraInfo    = info;
	optionCount++;
	return true;
}

bool CJavaVM::createVM()
{
	JavaVMInitArgs args = {0};
	JavaCreateVM   create;

	args.version  = JNI_VERSION_1_2;
	args.nOptions = optionCount;
	args.options  = options;

	create    = (JavaCreateVM)GetProcAddress(module, "JNI_CreateJavaVM");
	errorCode = create(&javaVM, &javaEnv, &args);

	if (errorCode != 0)
	{
		return false;
	}

	return true;
}

bool fileExist(char* file)
{
	HANDLE fileHandle = CreateFile(file, GENERIC_READ, FILE_SHARE_READ, NULL, OPEN_EXISTING, 0, NULL);

	if ((fileHandle == NULL) || (fileHandle == INVALID_HANDLE_VALUE))
	{
		return false;
	}

	CloseHandle(fileHandle);
	return true;
}

#ifdef _CONSOLE
int main(int argc, char* argv[])
#else
int WINAPI WinMain(HINSTANCE hInstance, HINSTANCE hPrevInstance, LPSTR lpCmdLine, int nShowCmd)
#endif
{
	CJavaVM pJava;
	JNIEnv* pEnv;

#ifndef _CONSOLE
	int   argc   = __argc;
	char* argv[] = __argv;
#endif

	// Add AGI.JAR to the ClassPath.
	{
		CStackAlloc temp;
		char*       path;
		char*       fullPath;
		char*       file;

		path     = (char*)temp.alloc(MAX_PATH * 2);
		fullPath = (char*)temp.alloc(MAX_PATH * 2);

		GetModuleFileName(NULL, path, MAX_PATH * 2);
		GetFullPathName(path, MAX_PATH * 2, fullPath, &file);

		strcpy(file, "agi.jar");

		if (!fileExist(fullPath))
		{
			file--;

			if (*file == '\\')
			{
				*file = 0;
			}
		}

		pJava.addClassPath(fullPath);
	}

	if (pJava.createVM())
	{
		CStackAlloc  temp;
		jclass       pMain;
		jclass       pString;
		jmethodID    pMainMethod;
		jobjectArray pArgs;

		// Find Classes
		pEnv    = pJava.getJavaEnv();
		pString = pEnv->FindClass("java/lang/String");

		if (pString == NULL)
		{
#ifdef _CONSOLE
			fputs(temp.loadString(IDS_NOJAVALANGSTRING),
					stderr);
#else
			MessageBox(
					NULL,
					temp.loadString(IDS_NOJAVALANGSTRING),
					temp.loadString(IDS_FATALERROR),
					MB_OK | MB_ICONERROR);
#endif
			return -1;
		}

#ifdef _DEBUG
		pMain = pEnv->FindClass("com/sierra/agi/tools/AGId");
#else
		pMain = pEnv->FindClass("com/sierra/agi/tools/AGI");
#endif

		if (pMain == NULL)
		{
#ifdef _CONSOLE
			fputs(temp.loadString(IDS_NOAGIJAR),
					stderr);
#else
			MessageBox(
					NULL,
					temp.loadString(IDS_NOAGIJAR),
					temp.loadString(IDS_FATALERROR),
					MB_OK | MB_ICONERROR);
#endif
			return -1;
		}

		// Find Main Method
		pMainMethod = pEnv->GetStaticMethodID(pMain, "main", "([Ljava/lang/String;)V");

		// Allocate Calling Arguments
		pArgs = pEnv->NewObjectArray(argc - 1, pString, NULL);

		for (int i = 1; i < __argc; i++)
		{
			pEnv->SetObjectArrayElement(pArgs, i - 1, pEnv->NewStringUTF(argv[i]));
		}

		// Call Main!
		pEnv->CallStaticVoidMethod(pMain, pMainMethod, pArgs);
	}
	else
	{
		CStackAlloc temp;

#ifdef _CONSOLE
		fputs(temp.loadString(IDS_NOVM),
				stderr);
#else
		MessageBox(
				NULL,
				temp.loadString(IDS_NOVM),
				temp.loadString(IDS_FATALERROR),
				MB_OK | MB_ICONERROR);
#endif
		return -1;
	}

	return 0;
}
