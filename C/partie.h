#include <stdlib.h>
#include <stdio.h>
#include <string.h>

#include "scrabble.h"
#include "users.h"


typedef struct partie
{
	scrabble* sc;
	users* user;
	users* other_users;
	char* chrono;
	char* score;
	char* phase;
	char* num_tour;
}partie;


partie* initPartie();

void partiePrint(partie* p);
