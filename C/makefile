
GCC=gcc
GPP=g++

FLAGS=-Wall -g -fdiagnostics-color=auto -D DEBUG
#FLAGS=-Wall -g -fdiagnostics-color=auto
GCCFLAGS=  -g -pthread
CPPFLAGS= 
LIBS= -lpthread
GTKPACKAGE = `pkg-config --cflags gtk+-3.0`
GTKLIBS = `pkg-config --libs gtk+-3.0`
GTKFLAGS = -rdynamic
# User definitions must be here

#executable
EXEC = main.x

#.h
INCS = scrabble.h \
		client.h \
		users.h \
		partie.h \
		ui.h
	
#.c
SOURCES = client.c \
	 	scrabble.c \
	 	users.c \
	 	partie.c \
	 	ui.c
	 

OBJS = $(SOURCES:.c=.o)


# Building the world
all: $(EXEC) 

$(EXEC): $(INCS) $(OBJS) 
	$(GCC) $(GCCFLAGS) $(OBJS) $(LIBS) -o $(EXEC) $(GTKLIBS) $(GTKFLAGS)


.SUFFIXES:
.SUFFIXES: .c .cc .o

.cc.o:
	$(GPP) $(FLAGS) -c $< $(GTKPACKAGE)

.c.o:
	$(GCC) $(FLAGS) -c $< $(GTKPACKAGE)


# Clean up
clean:
	rm -f *.o *.tmp* 
	rm -f $(EXEC) 

# Dependencies
depend: 

	$(GCC) -M $(CPPFLAGS) $(SOURCES) > .depend
include .depend
