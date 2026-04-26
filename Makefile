# Only i and god knows how this make it compile
# sence i frogot how it works only god knows how it works

# --- CONFIGURATION ---
SDK_ROOT = C:/android_sdk
PLATFORM_JAR = $(SDK_ROOT)/platforms/android-21/android.jar
BUILD_TOOLS = $(SDK_ROOT)/build-tools/21.1.2

# Tools
JAVAC = javac
java7 = C:/Users/VukiYT2011/AppData/Local/Programs/Eclipse\ Adoptium/jdk-8.0.482.8-hotspot/bin/
DX = $(BUILD_TOOLS)/dx.bat
# File that makes it work on java7 (it just edits the java.exe)
DXEVIL = $(BUILD_TOOLS)/dxjava7.bat
AAPT = $(BUILD_TOOLS)/aapt.exe
APKSIGNER = $(BUILD_TOOLS)/apksigner.bat

# Project Paths
GEN_DIR = gen
OBJ_DIR = obj
BIN_DIR = bin
LIBS_DIR = libs

# Library JARs
CLASSPATH = "obj"


# --- BUILD TARGETS ---

all: clean setup build_res compile_java dex_and_package sign alignnew

setup:
	mkdir -p $(GEN_DIR) $(OBJ_DIR) $(BIN_DIR)

clean:
	clear
	rm -rf $(GEN_DIR) $(OBJ_DIR) $(BIN_DIR)
superclean:
	rm -rf $(GEN_DIR) $(OBJ_DIR) $(BIN_DIR)
	rm app.apk java_files.txt
build_res:
	$(AAPT) package -f -m \
		-J $(GEN_DIR) \
		-M AndroidManifest.xml \
		-S res \
		-I $(PLATFORM_JAR) \
		-F $(BIN_DIR)/resources.apk
		
build_resnew:
	$(AAPT) package -f -m \
		-J $(GEN_DIR) \
		-M newandroid/AndroidManifest.xml \
		-S res \
		-I $(PLATFORM_JAR) \
		-F $(BIN_DIR)/resources.apk

compile_java:
	/usr/bin/find src -name "*.java" > java_files.txt	
	$(java7)javac -source 1.7 -target 1.7 -Xmaxerrs 1000 \
		-bootclasspath $(PLATFORM_JAR) \
		-d obj \
		-cp $(CLASSPATH) \
		gen/com/vukidev/tapzaster/R.java \
		@java_files.txt \
		-Xlint:-options
		
dex_and_packagenew:
	$(AAPT) package -f -M newandroid/AndroidManifest.xml -S res -A assets -I $(PLATFORM_JAR) -F bin\app-unsigned.apk
	cmd.exe /c "$(DXEVIL) --dex --output=bin\\classes.dex obj\\"
	cd $(BIN_DIR) && jar -uf app-unsigned.apk classes.dex
#cmd.exe /c "$(DXEVIL) --dex --output=bin\\classes.dex obj\\ $(LIBS_DIR)\\*.jar"
#cp $(BIN_DIR)/resources.apk $(BIN_DIR)/app-unsigned.apk
dex_and_package:
	$(AAPT) package -f -M AndroidManifest.xml -S res -A assets -I $(PLATFORM_JAR) -F bin\app-unsigned.apk
	cmd.exe /c "$(DXEVIL) --dex --output=bin\\classes.dex obj\\"
	cd $(BIN_DIR) && jar -uf app-unsigned.apk classes.dex
	
sign:
	# if u get an error here type "make generatekey"
	jarsigner -verbose -sigalg SHA1withRSA -digestalg SHA1 -keystore k.keystore -storepass android -keypass android $(BIN_DIR)/app-unsigned.apk nameofalias
	cp "bin/app-unsigned.apk" "bin/app-signed.apk"
	
alignnew:
	#due to usage of 1.8.0 java. I dont have zipalign for usage sorr not sorr /shrug.
	cp bin/app-signed.apk app.apk
	#FINISHED
	
	# app.L.apk this version of the app runs on android 1.0 to 14
	# app.apk runs on android 10 - 16 (latest when this text was added 31/03/2026 (happy 1st april of 2026))
	# due to me figuring out how to android now its 1 file aka app.apk (added 23/4/2026)

generatekey:
	keytool -genkey -v -keystore k.keystore -alias nameofalias -keyalg RSA -keysize 2048 -validity 10000 -storepass android -keypass android -dname "CN=VukiDev,O=VukiDev,C=US"