#ifndef UI_H
#define UI_H

#include <gtk/gtk.h>
#include <glib/gprintf.h>
#include <stdlib.h>
#include <pthread.h>
#include <string.h>
#include "scrabble.h"




char* forGtkView(char* grille,char*tirage);

char* forSendfromGtk(gchar* text);

void set_new_buffer_withmarkup(GtkTextBuffer* scrabble_text_view,char* grille,char* tirage);

void set_chat_text(GtkTextBuffer* chat_buffer,char* name,char* message);

#endif