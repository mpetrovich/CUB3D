PRODUCT    = CUB3D
VERSION    = 0.2.5

PACKAGE    = java3D
DOCS       = docs

MAIN       = CUB3D
MANIFEST   = Manifest
EXECUTABLE = $(PRODUCT)\ $(VERSION).jar


make:
	javac *.java $(PACKAGE)/*.java $(PACKAGE)/storage/*.java $(PACKAGE)/render/*.java $(PACKAGE)/io/*.java

jar:
	jar cmf0 $(MANIFEST) $(EXECUTABLE) *.class $(PACKAGE)/*.class $(PACKAGE)/storage/*.class $(PACKAGE)/render/*.class $(PACKAGE)/io/*.class

docs:
	mkdir -p $(DOCS)
	javadoc -d $(DOCS) -subpackages $(PACKAGE)

all:
	make cleanall
	make
	make docs

launch:
	make all
	make run

run:
	java $(MAIN)

release:
	make all
	make jar
	make clean

clean:
	rm -f *.class $(PACKAGE)/*.class $(PACKAGE)/storage/*.class $(PACKAGE)/render/*.class $(PACKAGE)/io/*.class

cleanjar:
	rm -f $(EXECUTABLE)

cleandocs:
	rm -Rf $(DOCS)

cleanall:
	make clean
	make cleanjar
	make cleandocs
