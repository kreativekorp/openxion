INSTJARDIR = /usr/lib
INSTBINDIR = /usr/bin
SRCFILES = src/test/*.java src/com/kreative/*/*.java src/com/kreative/*/*/*.java src/com/kreative/*/*/*/*.java
PACKAGES = com.kreative.openxion com.kreative.openxion.ast com.kreative.openxion.io com.kreative.openxion.math com.kreative.openxion.util com.kreative.openxion.xom com.kreative.openxion.xom.inst com.kreative.openxion.xom.type com.kreative.xiondoc

all: clean bin doc osxclean OpenXION.jar XIONDoc.jar OpenXION-src.tgz XIONDoc-src.tgz OpenXION-test.tgz OpenXION-xndocs.tgz XION-Language-Module.tgz

eclipseall: eclipseclean osxclean OpenXION.jar XIONDoc.jar OpenXION-src.tgz XIONDoc-src.tgz OpenXION-test.tgz OpenXION-xndocs.tgz XION-Language-Module.tgz

clean:
	rm -rf bin
	rm -rf doc
	rm -rf OpenXION*.jar
	rm -rf OpenXION*.tgz
	rm -rf XIONDoc*.jar
	rm -rf XIONDoc*.tgz
	rm -rf XION-*.jar
	rm -rf XION-*.tgz
	rm -rf xndoc/doc.htmld

eclipseclean:
	rm -rf OpenXION*.jar
	rm -rf OpenXION*.tgz
	rm -rf XIONDoc*.jar
	rm -rf XIONDoc*.tgz
	rm -rf XION-*.jar
	rm -rf XION-*.tgz
	rm -rf xndoc/doc.htmld

bin:
	mkdir -p bin
	javac -sourcepath src $(SRCFILES) -d bin
	cp src/com/kreative/xiondoc/*.css bin/com/kreative/xiondoc/

doc:
	mkdir -p doc
	javadoc -sourcepath src $(PACKAGES) -d doc

osxclean:
	export COPYFILE_DISABLE=true
	rm -f src/.DS_Store
	rm -f src/*/.DS_Store
	rm -f src/*/*/.DS_Store
	rm -f src/*/*/*/.DS_Store
	rm -f src/*/*/*/*/.DS_Store
	rm -f src/*/*/*/*/*/.DS_Store
	rm -f src/*/*/*/*/*/*/.DS_Store
	rm -f src/*/*/*/*/*/*/*/.DS_Store
	rm -f src/*/*/*/*/*/*/*/*/.DS_Store
	rm -f bin/.DS_Store
	rm -f bin/*/.DS_Store
	rm -f bin/*/*/.DS_Store
	rm -f bin/*/*/*/.DS_Store
	rm -f bin/*/*/*/*/.DS_Store
	rm -f bin/*/*/*/*/*/.DS_Store
	rm -f bin/*/*/*/*/*/*/.DS_Store
	rm -f bin/*/*/*/*/*/*/*/.DS_Store
	rm -f bin/*/*/*/*/*/*/*/*/.DS_Store
	rm -f "Sample Scripts"/.DS_Store
	rm -f XIONStdTestSuite/.DS_Store

OpenXION.jar: osxclean
	jar cmf dep/MANIFEST-OXN.MF OpenXION.jar -C bin com/kreative/openxion

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
	tar -czf OpenXION-xndocs.tgz -C xndoc doc.xnd xiondoc-1.0.dtd docs
	mv xndoc/docs xndoc/doc.htmld

XION-Language-Module.tgz:
	tar -czf XION-Language-Module.tgz -C bbedit XION.plist

test: OpenXION.jar
	java -jar OpenXION.jar -T XIONStdTestSuite/*.xn

localuninstall:
	rm xe
	rm xion
	rm xiondoc

localinstall: OpenXION.jar XIONDoc.jar
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
	rm -f $(INSTBINDIR)/xe
	rm -f $(INSTBINDIR)/xion
	rm -f $(INSTBINDIR)/xiondoc

install: OpenXION.jar XIONDoc.jar
	cp -f OpenXION.jar $(INSTJARDIR)/OpenXION.jar
	cp -f XIONDoc.jar $(INSTJARDIR)/XIONDoc.jar
	echo "#!/bin/sh" > $(INSTBINDIR)/xe
	echo "#!/bin/sh" > $(INSTBINDIR)/xion
	echo "#!/bin/sh" > $(INSTBINDIR)/xiondoc
	echo 'java -Xmx1024M -jar "$(INSTJARDIR)/OpenXION.jar" -e "$$*"' >> $(INSTBINDIR)/xe
	echo 'java -Xmx1024M -jar "$(INSTJARDIR)/OpenXION.jar" "$$@"' >> $(INSTBINDIR)/xion
	echo 'java -Xmx1024M -jar "$(INSTJARDIR)/XIONDoc.jar" "$$@"' >> $(INSTBINDIR)/xiondoc
	chmod +x $(INSTBINDIR)/xe
	chmod +x $(INSTBINDIR)/xion
	chmod +x $(INSTBINDIR)/xiondoc

.PHONY: all eclipseall clean eclipseclean osxclean test localuninstall localinstall uninstall install
