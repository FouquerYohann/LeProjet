#include <stdlib.h>
#include <stdio.h>
#include <string.h>


typedef struct user{
	char* name;
	char* score;
	struct user* next;

}users;

users* initUser(char* n, char* sc);

void freeUser(users* user);

void freeAllUser(users* user);

int numUser(users* user);

users* addUser(users* us,users* user);

users* addUserNameScore(users* us,char* name,char* score);

users* getUserByName(users* us, char* name);

char* getScoreUser(users* us, users* user);

users* deleteUser(users* us,users* user);

char* toStringUsers(users* us);
