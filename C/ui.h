#include <gtk/gtk.h>
#include <glib/gprintf.h>
#include <stdlib.h>


G_MODULE_EXPORT void on_click_trouve(GtkWidget *widget, gpointer data);

G_MODULE_EXPORT void on_click_send(GtkWidget *widget, gpointer data);

G_MODULE_EXPORT void on_click_sort(GtkWidget *widget, gpointer data);

gchar* forGtkView(char* grille,char*tirage);

char* forSendfromGtk(gchar* text);