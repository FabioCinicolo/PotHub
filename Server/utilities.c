#include <math.h>
#include <stdio.h>
#include <stdlib.h>
#include "Headers/utilities.h"

double toRad(double x)
{
    return x * M_PI / 180;
}

double haversineDistance(double latitude1, double longitude1, double latitude2, double longitude2)
{
    double earth_radius = 6378137.0;
    double d_lat = toRad(latitude2 - latitude1);
    double d_long = toRad(longitude2 - longitude1);
    double a = sin(d_lat / 2) * sin(d_lat / 2) + cos(toRad(latitude1)) * cos(toRad(latitude2)) * sin(d_long / 2) * sin(d_long / 2);
    double c = 2 * atan2(sqrt(a), sqrt(1 - a));
    return earth_radius * c;
}


char *substr(const char *src, int m, int n)
{
    // get the length of the destination string
    int len = n - m;

    // allocate (len + 1) chars for destination (+1 for extra null character)
    char *dest = (char *)malloc(sizeof(char) * (len + 1));

    // extracts characters between m'th and n'th index from source string
    // and copy them into the destination string
    for (int i = m; i < n && (*(src + i) != '\0'); i++)
    {
        *dest = *(src + i);
        dest++;
    }

    // null-terminate the destination string
    *dest = '\0';

    // return the destination string
    return dest - len;
}

int valid_date(int day, int mon, int year)
{
    int is_valid = 1, is_leap = 0;

    if (year >= 1800 && year <= 9999)
    {

        //  check whether year is a leap year
        if ((year % 4 == 0 && year % 100 != 0) || (year % 400 == 0))
        {
            is_leap = 1;
        }

        // check whether mon is between 1 and 12
        if (mon >= 1 && mon <= 12)
        {
            // check for days in feb
            if (mon == 2)
            {
                if (is_leap && day == 29)
                {
                    is_valid = 1;
                }
                else if (day > 28)
                {
                    is_valid = 0;
                }
            }

            // check for days in April, June, September and November
            else if (mon == 4 || mon == 6 || mon == 9 || mon == 11)
            {
                if (day > 30)
                {
                    is_valid = 0;
                }
            }

            // check for days in rest of the months
            // i.e Jan, Mar, May, July, Aug, Oct, Dec
            else if (day > 31)
            {
                is_valid = 0;
            }
        }

        else
        {
            is_valid = 0;
        }
    }
    else
    {
        is_valid = 0;
    }

    return is_valid;
}

int getDateDifference(char *date1, char *date2, int *day_difference, int *month_difference, int *year_difference)
{

    int day1, day2, mon1, mon2, year1, year2;

    day1 = atoi(substr(date1, 0, 2));
    mon1 = atoi(substr(date1, 3, 5));
    year1 = atoi(substr(date1, 6, 10));

    day2 = atoi(substr(date2, 0, 2));
    mon2 = atoi(substr(date2, 3, 5));
    year2 = atoi(substr(date2, 6, 10));

    if (!valid_date(day1, mon1, year1))
    {
        printf("First date is invalid.\n");
        return 1;
    }

    if (!valid_date(day2, mon2, year2))
    {
        printf("Second date is invalid.\n");
        return 1;
    }

    if (day2 < day1)
    {
        // borrow days from february
        if (mon2 == 3)
        {
            //  check whether year is a leap year
            if ((year2 % 4 == 0 && year2 % 100 != 0) || (year2 % 400 == 0))
            {
                day2 += 29;
            }

            else
            {
                day2 += 28;
            }
        }

        // borrow days from April or June or September or November
        else if (mon2 == 5 || mon2 == 7 || mon2 == 10 || mon2 == 12)
        {
            day2 += 30;
        }

        // borrow days from Jan or Mar or May or July or Aug or Oct or Dec
        else
        {
            day2 += 31;
        }

        mon2 = mon2 - 1;
    }

    if (mon2 < mon1)
    {
        mon2 += 12;
        year2 -= 1;
    }

    *day_difference = day2 - day1;
    *month_difference = mon2 - mon1;
    *year_difference = year2 - year1;

    return 0;
}
