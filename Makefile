# Java compiler
JAVAC = javac

# Java compiler flags
JAVAFLAGS = -g 

# Creating a .class file
COMPILE = $(JAVAC) $(JAVAFLAGS)

# One of these should be the "main" class listed in Runfile
CLASS_FILES = Interpreter.class BinaryTree.class

# The first target is the one that is executed when you invoke
# "make". 

all: $(CLASS_FILES) 

# The line describing the action starts with <TAB>
%.class : %.java
	$(COMPILE) $<