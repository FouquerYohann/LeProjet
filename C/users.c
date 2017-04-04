#include "users.h"

users* initUser(char* n, char* sc){
	users* ret=(users*)malloc(sizeof(users));
	ret->name=strdup(n);
	ret->score=strdup(sc);
	ret->next=NULL;
	return ret;
}

void freeUser(users* user){
	free(user->name);
	free(user->score);
	free(user);
}

void freeAllUser(users* user){
	users* next=user->next;
	freeUser(user);
	freeAllUser(next);
}

int numUser(users* user){
	int ret=1;
	users* curr=user;
	while(curr->next){
		ret++;
		curr=curr->next;
	}
	return ret;
}

users* addUser(users* us,users* user){
	if(user==NULL){
		fprintf(stderr, "user est null dans adduser\n");
		return NULL;
	}
	user->next=us;
	return user;
}

users* addUserNameScore(users* us,char* name,char* score){
	users* user=initUser(name,score);
	return addUser(us,user);
}

users* getUserByName(users* us, char* name){
	if(us == NULL){
		return NULL;
	}
	users* curr=us;

	while(curr->next){
		if(strcmp(curr->name,name)==0)
			return curr;
		curr=curr->next;
	}
	return NULL;
}

char* getScoreUser(users* us, users* user){
	users* curr=us;

	while(curr->next){
		if(us==user)
			return curr->score;
	}
	return NULL;
}

users* deleteUser(users* us,users* user){
	if(us == NULL || user== NULL){
		fprintf(stderr, "NO users in deleteUser\n");
		return NULL;
	}

	if(us==user){
		users* next=us->next;
		freeUser(us);
		return next;
	}

	users* tmp=us;
	users* prec=NULL;

	while(tmp->next != NULL){
		if(tmp == user){
			prec->next=tmp->next;
			freeUser(tmp);
			return us;
		}
		prec=tmp;
		tmp=tmp->next;
	}

	return us;

}



char* toStringUsers(users* us){
	if(!us){
		return " ";
	}
	char* buf=(char*)malloc(1024*sizeof(char));
	users* curr=us;
	while(curr->next){
		strcat(buf,curr->name);
		strcat(buf," : ");
		strcat(buf,curr->score);
		strcat(buf, "\n");
		curr=curr->next;	
	}
	return buf;
}
