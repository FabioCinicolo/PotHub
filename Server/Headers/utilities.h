#ifndef UTILITIES_H
#define UTILITIES_H

double haversineDistance(double latitude1, double longitude1, double latitude2, double longitude2);

double toRad(double x);

char *substr(const char *src, int m, int n);

int valid_date(int day, int mon, int year);

int getDateDifference(char *date1, char *date2, int *day_difference, int *month_difference, int *year_difference);

#endif