INSTJARDIR = /usr/lib
INSTBINDIR = /usr/bin
CLASSPATH := $(shell find dep -name \*.jar | tr '\n' : | sed s/:$$//)
SRCFILES := $(shell find src -name \*.java)
RESFILES := $(shell find src -type f \! -name .\* \! -name \*.java | sed s/^src\\///)
PACKAGES := $(shell find src -name \*.java | sed -E s/^src\\/\|\\/[^\\/]+$$//g | sort | uniq | tr / .)

all: clean bin doc osxclean OpenXION.jar XIONDoc.jar OpenXION-src.tgz XIONDoc-src.tgz OpenXION-test.tgz OpenXION-xndocs.tgz XION-Language-Module.tgz OpenXION-dist.zip OpenXION-distwin.zip

eclipseall: eclipseclean osxclean OpenXION.jar XIONDoc.jar OpenXION-src.tgz XIONDoc-src.tgz OpenXION-test.tgz OpenXION-xndocs.tgz XION-Language-Module.tgz OpenXION-dist.zip OpenXION-distwin.zip

clean:
	rm -rf bin
	rm -rf doc
	rm -rf OpenXION*.jar
	rm -rf OpenXION*.tgz
	rm -rf OpenXION*.zip
	rm -rf XIONDoc*.jar
	rm -rf XIONDoc*.tgz
	rm -rf XION-*.jar
	rm -rf XION-*.tgz
	rm -rf xndoc/doc.htmld
	rm -rf dist

eclipseclean:
	rm -rf OpenXION*.jar
	rm -rf OpenXION*.tgz
	rm -rf OpenXION*.zip
	rm -rf XIONDoc*.jar
	rm -rf XIONDoc*.tgz
	rm -rf XION-*.jar
	rm -rf XION-*.tgz
	rm -rf xndoc/doc.htmld
	rm -rf dist

bin:
	mkdir -p bin
	javac -cp "./bin:$(CLASSPATH)" -sourcepath src $(SRCFILES) -d bin
	$(foreach res,$(RESFILES),cp src/$(res) bin/$(res);)

doc:
	mkdir -p doc
	javadoc -cp "./bin:$(CLASSPATH)" -sourcepath src $(PACKAGES) -d doc

osxclean:
	export COPYFILE_DISABLE=true
	find src -name .DS_Store -delete
	find bin -name .DS_Store -delete
	find "Sample Scripts" -name .DS_Store -delete
	find XIONStdTestSuite -name .DS_Store -delete

OpenXION.jar: osxclean
	rm -rf bin/META-INF
	mkdir -p bin/META-INF/services
	cp dep/javax.script.ScriptEngineFactory bin/META-INF/services/
	jar cmf dep/MANIFEST-OXN.MF OpenXION.jar -C bin META-INF -C bin com/kreative/openxion
	rm -rf bin/META-INF

XIONDoc.jar: osxclean
	jar cmf dep/MANIFEST-XND.MF XIONDoc.jar -C bin com/kreative/xiondoc

OpenXION-src.tgz: osxclean
	tar -czf OpenXION-src.tgz src/com/kreative/openxion LICENSE

XIONDoc-src.tgz: osxclean
	tar -czf XIONDoc-src.tgz src/com/kreative/xiondoc LICENSE

OpenXION-test.tgz: osxclean
	tar -czf OpenXION-test.tgz "Sample Scripts" XIONStdTestSuite

OpenXION-xndocs.tgz: XIONDoc.jar
	java -jar XIONDoc.jar xndoc/doc.xnd > /dev/null
	mv xndoc/doc.htmld xndoc/docs
	tar -czf OpenXION-xndocs.tgz -C xndoc doc.xnd xiondoc-1.3.dtd docs
	mv xndoc/docs xndoc/doc.htmld

XION-Language-Module.tgz:
	tar -czf XION-Language-Module.tgz -C bbedit XION.plist

OpenXION-dist.zip: OpenXION.jar XIONDoc.jar
	rm -rf dist
	mkdir dist
	cp OpenXION.jar dist/OpenXION.jar
	cp XIONDoc.jar dist/XIONDoc.jar
	cp dep/jline.jar dist/jline.jar
	cp README dist/README
	cp LICENSE dist/LICENSE
	cp dep/JLINE-LICENSE dist/JLINE-LICENSE
	echo "#!/bin/sh" > dist/xe
	echo "#!/bin/sh" > dist/xion
	echo "#!/bin/sh" > dist/xiondoc
	echo 'java -Xmx1024M -jar OpenXION.jar -e "$$*"' >> dist/xe
	echo 'java -Xmx1024M -jar OpenXION.jar "$$@"' >> dist/xion
	echo 'java -Xmx1024M -jar XIONDoc.jar "$$@"' >> dist/xiondoc
	chmod +x dist/xe
	chmod +x dist/xion
	chmod +x dist/xiondoc
	echo "#!/bin/sh" > dist/install
	echo 'cp -f OpenXION.jar /usr/lib/OpenXION.jar' >> dist/install
	echo 'cp -f XIONDoc.jar /usr/lib/XIONDoc.jar' >> dist/install
	echo 'cp -f jline.jar /usr/lib/jline.jar' >> dist/install
	echo 'echo "#!/bin/sh" > /usr/bin/xe' >> dist/install
	echo 'echo "#!/bin/sh" > /usr/bin/xion' >> dist/install
	echo 'echo "#!/bin/sh" > /usr/bin/xiondoc' >> dist/install
	echo "echo 'java -Xmx1024M -jar \"/usr/lib/OpenXION.jar\" -e \"\$$*\"' >> /usr/bin/xe" >> dist/install
	echo "echo 'java -Xmx1024M -jar \"/usr/lib/OpenXION.jar\" \"\$$@\"' >> /usr/bin/xion" >> dist/install
	echo "echo 'java -Xmx1024M -jar \"/usr/lib/XIONDoc.jar\" \"\$$@\"' >> /usr/bin/xiondoc" >> dist/install
	echo 'chmod +x /usr/bin/xe' >> dist/install
	echo 'chmod +x /usr/bin/xion' >> dist/install
	echo 'chmod +x /usr/bin/xiondoc' >> dist/install
	chmod +x dist/install
	zip -j OpenXION-dist.zip dist/*
	rm -rf dist

OpenXION-distwin.zip: OpenXION.jar XIONDoc.jar
	rm -rf dist
	mkdir dist
	cp OpenXION.jar dist/OpenXION.jar
	cp XIONDoc.jar dist/XIONDoc.jar
	cp dep/jline.jar dist/jline.jar
	cp README dist/README.TXT
	cp LICENSE dist/LICENSE.TXT
	cp dep/JLINE-LICENSE dist/JLINE-LICENSE.TXT
	printf '@ECHO OFF\r\n' > dist/xe.bat
	printf '@ECHO OFF\r\n' > dist/xion.bat
	printf '@ECHO OFF\r\n' > dist/xiondoc.bat
	printf 'java -Xmx1024M -jar "%%~dp0\\OpenXION.jar" -e "%%*"\r\n' >> dist/xe.bat
	printf 'java -Xmx1024M -jar "%%~dp0\\OpenXION.jar" %%*\r\n' >> dist/xion.bat
	printf 'java -Xmx1024M -jar "%%~dp0\\XIONDoc.jar" %%*\r\n' >> dist/xiondoc.bat
	printf '@ECHO OFF\r\n' > dist/install.bat
	printf 'COPY /B "%%~dp0\\OpenXION.jar" "%%SYSTEMROOT%%\\System32\\OpenXION.jar"\r\n' >> dist/install.bat
	printf 'COPY /B "%%~dp0\\XIONDoc.jar" "%%SYSTEMROOT%%\\System32\\XIONDoc.jar"\r\n' >> dist/install.bat
	printf 'COPY /B "%%~dp0\\jline.jar" "%%SYSTEMROOT%%\\System32\\jline.jar"\r\n' >> dist/install.bat
	printf 'echo @ECHO OFF > "%%SYSTEMROOT%%\\System32\\xe.bat"\r\n' >> dist/install.bat
	printf 'echo @ECHO OFF > "%%SYSTEMROOT%%\\System32\\xion.bat"\r\n' >> dist/install.bat
	printf 'echo @ECHO OFF > "%%SYSTEMROOT%%\\System32\\xiondoc.bat"\r\n' >> dist/install.bat
	printf 'echo java -Xmx1024M -jar "%%SYSTEMROOT%%\\System32\\OpenXION.jar" -e "%%%%*" >> "%%SYSTEMROOT%%\\System32\\xe.bat"\r\n' >> dist/install.bat
	printf 'echo java -Xmx1024M -jar "%%SYSTEMROOT%%\\System32\\OpenXION.jar" %%%%* >> "%%SYSTEMROOT%%\\System32\\xion.bat"\r\n' >> dist/install.bat
	printf 'echo java -Xmx1024M -jar "%%SYSTEMROOT%%\\System32\\XIONDoc.jar" %%%%* >> "%%SYSTEMROOT%%\\System32\\xiondoc.bat"\r\n' >> dist/install.bat
	zip -j OpenXION-distwin.zip dist/*
	rm -rf dist

test: OpenXION.jar
	java -jar OpenXION.jar -s allow -T XIONStdTestSuite/*.xn

xndocs: XIONDoc.jar
	java -jar XIONDoc.jar xndoc/doc.xnd

localuninstall:
	rm -f jline.jar
	rm -f xe
	rm -f xion
	rm -f xiondoc

localinstall: OpenXION.jar XIONDoc.jar
	cp -f dep/jline.jar jline.jar
	echo "#!/bin/sh" > xe
	echo "#!/bin/sh" > xion
	echo "#!/bin/sh" > xiondoc
	echo 'java -Xmx1024M -jar OpenXION.jar -e "$$*"' >> xe
	echo 'java -Xmx1024M -jar OpenXION.jar "$$@"' >> xion
	echo 'java -Xmx1024M -jar XIONDoc.jar "$$@"' >> xiondoc
	chmod +x xe
	chmod +x xion
	chmod +x xiondoc

uninstall:
	rm -f $(INSTJARDIR)/OpenXION.jar
	rm -f $(INSTJARDIR)/XIONDoc.jar
	rm -f $(INSTJARDIR)/jline.jar
	rm -f $(INSTBINDIR)/xe
	rm -f $(INSTBINDIR)/xion
	rm -f $(INSTBINDIR)/xiondoc

install: OpenXION.jar XIONDoc.jar
	cp -f OpenXION.jar $(INSTJARDIR)/OpenXION.jar
	cp -f XIONDoc.jar $(INSTJARDIR)/XIONDoc.jar
	cp -f dep/jline.jar $(INSTJARDIR)/jline.jar
	echo "#!/bin/sh" > $(INSTBINDIR)/xe
	echo "#!/bin/sh" > $(INSTBINDIR)/xion
	echo "#!/bin/sh" > $(INSTBINDIR)/xiondoc
	echo 'java -Xmx1024M -jar "$(INSTJARDIR)/OpenXION.jar" -e "$$*"' >> $(INSTBINDIR)/xe
	echo 'java -Xmx1024M -jar "$(INSTJARDIR)/OpenXION.jar" "$$@"' >> $(INSTBINDIR)/xion
	echo 'java -Xmx1024M -jar "$(INSTJARDIR)/XIONDoc.jar" "$$@"' >> $(INSTBINDIR)/xiondoc
	chmod +x $(INSTBINDIR)/xe
	chmod +x $(INSTBINDIR)/xion
	chmod +x $(INSTBINDIR)/xiondoc

.PHONY: all eclipseall clean eclipseclean osxclean test xndocs localuninstall localinstall uninstall install
