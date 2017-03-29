
GCC=gcc
GPP=g++

FLAGS=-Wall -g -fdiagnostics-color=auto -D DEBUG
#FLAGS=-Wall -g -fdiagnostics-color=auto
GCCFLAGS=  -g -pthread
CPPFLAGS= 
LIBS= -lm

# User definitions must be here

#executable
EXEC = main.x

#.h
INCS = scrabble.h \
		client.h \
		users.h \
		partie.h

#.c
SOURCES = client.c \
	 	scrabble.c \
	 	users.c \
	 	partie.c

OBJS = $(SOURCES:.c=.o)


# Building the world
all: $(EXEC) 

$(EXEC): $(INCS) $(OBJS) 
	$(GCC) $(GCCFLAGS) $(OBJS) $(LIBS) -o $(EXEC) 


.SUFFIXES:
.SUFFIXES: .c .cc .o

.cc.o:
	$(GPP) $(FLAGS) -c $<

.c.o:
	$(GCC) $(FLAGS) -c $<


# Clean up
clean:
	rm -f *.o *.tmp* 
	rm -f $(EXEC) 

# Dependencies
depend: 

	$(GCC) -M $(CPPFLAGS) $(SOURCES) > .depend
include .depend
