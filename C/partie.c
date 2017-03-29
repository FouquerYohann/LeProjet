#include "partie.h"

partie* initPartie(){
	partie* p=(partie*) malloc(sizeof(partie));
	p->sc=initScrabble();
	p->user=NULL;
	p->chrono=NULL;
	p->score=NULL;
	p->phase=NULL;
	p->num_tour=NULL;

	return p;
}


void partiePrint(partie* p){
	 printf("%s\n", toString(p->sc));

	 printf("%s\n", toStringUsers(p->user));
	 printf("%s\n", toStringUsers(p->other_users));

}
