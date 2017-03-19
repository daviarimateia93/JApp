package japp.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public abstract class DateHelper {
	
	private static final String EMPTY = "";
	
	public static String DATE_TIME_FORMAT_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS";
	public static String DATE_FORMAT_PATTERN = "yyyy-MM-dd";
	public static String TIME_FORMAT_PATTERN = "HH:mm:ss.SSS";
	
	protected DateHelper() {
		
	}
	
	public static LocalDateTime setTimeZone(final LocalDateTime localDateTime, final String timeZone) {
		final ZoneId zoneId = ZoneId.of(timeZone);
		
		if (zoneId == null) {
			return localDateTime;
		} else {
			return localDateTime.atZone(zoneId).toLocalDateTime();
		}
	}
	
	public static Date setTimeZone(final Date date, final String timeZone) {
		final TimeZone foundTimeZone = TimeZone.getTimeZone(timeZone);
		
		if (foundTimeZone == null) {
			return date;
		} else {
			final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DateHelper.DATE_TIME_FORMAT_PATTERN);
			simpleDateFormat.setTimeZone(foundTimeZone);
			
			return parseDate(simpleDateFormat.format(date));
		}
	}
	
	public static Date parseDate(final String date) {
		return parseDate(date, DATE_TIME_FORMAT_PATTERN);
	}
	
	public static Date parseDate(final String date, final String pattern) {
		try {
			return new SimpleDateFormat(pattern).parse(date);
		} catch (final ParseException parseException) {
			return null;
		}
	}
	
	public static String formatDate(final Date date) {
		return formatDate(date, DATE_TIME_FORMAT_PATTERN);
	}
	
	public static String formatDate(final Date date, final String pattern) {
		return date == null ? EMPTY : new SimpleDateFormat(pattern).format(date);
	}
	
	public static LocalDateTime parseLocalDateTime(final String localDateTime) {
		return parseLocalDateTime(localDateTime, DATE_TIME_FORMAT_PATTERN);
	}
	
	public static LocalDateTime parseLocalDateTime(final String localDateTime, final String pattern) {
		try {
			return LocalDateTime.parse(localDateTime, DateTimeFormatter.ofPattern(pattern));
		} catch (final DateTimeParseException dateTimeParseException) {
			return null;
		}
	}
	
	public static String formatLocalDateTime(final LocalDateTime localDateTime) {
		return formatLocalDateTime(localDateTime, DATE_TIME_FORMAT_PATTERN);
	}
	
	public static String formatLocalDateTime(final LocalDateTime localDateTime, final String pattern) {
		return localDateTime.format(DateTimeFormatter.ofPattern(pattern));
	}
	
	public static LocalDate parseLocalDate(final String localDate) {
		return parseLocalDate(localDate, DATE_FORMAT_PATTERN);
	}
	
	public static LocalDate parseLocalDate(final String localDate, final String pattern) {
		try {
			return LocalDate.parse(localDate, DateTimeFormatter.ofPattern(pattern));
		} catch (final DateTimeParseException dateTimeParseException) {
			return null;
		}
	}
	
	public static String formatLocalDate(final LocalDate localDate) {
		return formatLocalDate(localDate, DATE_FORMAT_PATTERN);
	}
	
	public static String formatLocalDate(final LocalDate localDate, final String pattern) {
		return localDate.format(DateTimeFormatter.ofPattern(pattern));
	}
	
	public static LocalTime parseLocalTime(final String localTIme) {
		return parseLocalTime(localTIme, TIME_FORMAT_PATTERN);
	}
	
	public static LocalTime parseLocalTime(final String localTime, final String pattern) {
		try {
			return LocalTime.parse(localTime, DateTimeFormatter.ofPattern(pattern));
		} catch (final DateTimeParseException dateTimeParseException) {
			return null;
		}
	}
	
	public static String formatLocalTime(final LocalTime localTIme) {
		return formatLocalTime(localTIme, TIME_FORMAT_PATTERN);
	}
	
	public static String formatLocalTime(final LocalTime localTime, final String pattern) {
		return localTime.format(DateTimeFormatter.ofPattern(pattern));
	}
	
	public static Calendar toCalendar(final Date date) {
		final Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		
		return calendar;
	}
	
	public static Integer get(final Date date, final int type) {
		final Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		
		return calendar.get(type);
	}
	
	public static Date setFirstForTime(final Date date) {
		final Calendar calendar = toCalendar(date);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		
		return calendar.getTime();
	}
	
	public static Date setMiddleForTime(final Date date) {
		final Calendar calendar = toCalendar(date);
		calendar.set(Calendar.HOUR_OF_DAY, 11);
		calendar.set(Calendar.MINUTE, 59);
		calendar.set(Calendar.SECOND, 59);
		calendar.set(Calendar.MILLISECOND, 999);
		
		return calendar.getTime();
	}
	
	public static Date setLastForTime(final Date date) {
		final Calendar calendar = toCalendar(date);
		calendar.set(Calendar.HOUR_OF_DAY, 23);
		calendar.set(Calendar.MINUTE, 59);
		calendar.set(Calendar.SECOND, 59);
		calendar.set(Calendar.MILLISECOND, 999);
		
		return calendar.getTime();
	}
	
	public static Date setLastForYear(final Date date) {
		final Calendar calendar = toCalendar(date);
		calendar.set(Calendar.MONTH, calendar.getActualMaximum(Calendar.MONTH));
		
		return setLastForTime(setLastForMonth(calendar.getTime()));
	}
	
	public static Date setLastForMonth(final Date date) {
		final Calendar calendar = toCalendar(date);
		calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
		
		return setLastForTime(calendar.getTime());
	}
	
	public static Date setLastForWeek(final Date date) {
		final Calendar calendar = toCalendar(date);
		calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());
		calendar.add(Calendar.DAY_OF_YEAR, 6);
		
		return setLastForTime(calendar.getTime());
	}
	
	public static Date setLastForDay(final Date date) {
		return setLastForTime(date);
	}
	
	public static List<Date> getYearsBetween(final Date startDate, final Date endDate) {
		final List<Date> years = new ArrayList<>();
		
		if (endDate.compareTo(startDate) == -1) {
			return years;
		}
		
		for (int year = get(startDate, Calendar.YEAR); year <= get(endDate, Calendar.YEAR); year++) {
			final Calendar calendar = Calendar.getInstance();
			calendar.set(year, calendar.getActualMaximum(Calendar.MONTH), 1);
			
			years.add(setLastForYear(calendar.getTime()));
		}
		
		return years;
	}
	
	public static List<Date> getMonthsBetween(final Date startDate, final Date endDate) {
		final List<Date> months = new ArrayList<>();
		
		if (endDate.compareTo(startDate) == -1) {
			return months;
		}
		
		final List<Date> years = getYearsBetween(startDate, endDate);
		
		for (int i = 0; i < years.size(); i++) {
			for (int month = i == 0 ? get(startDate, Calendar.MONTH) : 0; month <= (i == years.size() - 1 ? get(endDate, Calendar.MONTH) : 11); month++) {
				final Calendar calendar = Calendar.getInstance();
				calendar.set(get(years.get(i), Calendar.YEAR), month, 1);
				
				months.add(setLastForMonth(calendar.getTime()));
			}
		}
		
		return months;
	}
	
	public static List<Date> getWeeksBetween(final Date startDate, final Date endDate) {
		final List<Date> weeks = new ArrayList<>();
		
		if (endDate.compareTo(startDate) == -1) {
			return weeks;
		}
		
		final List<Date> months = getMonthsBetween(startDate, endDate);
		
		for (int i = 0; i < months.size(); i++) {
			for (int week = i == 0 ? get(startDate, Calendar.WEEK_OF_MONTH) : 1; week <= (i == months.size() - 1 ? get(endDate, Calendar.WEEK_OF_MONTH) : toCalendar(months.get(i)).getActualMaximum(Calendar.WEEK_OF_MONTH)); week++) {
				final Calendar calendar = Calendar.getInstance();
				calendar.set(get(months.get(i), Calendar.YEAR), get(months.get(i), Calendar.MONTH), 1);
				calendar.set(Calendar.WEEK_OF_MONTH, week);
				
				weeks.add(setLastForWeek(calendar.getTime()));
			}
		}
		
		return weeks;
	}
	
	public static List<Date> getDaysBetween(final Date startDate, final Date endDate) {
		final List<Date> days = new ArrayList<>();
		
		if (endDate.compareTo(startDate) == -1) {
			return days;
		}
		
		final List<Date> months = getMonthsBetween(startDate, endDate);
		
		for (int i = 0; i < months.size(); i++) {
			for (int day = i == 0 ? get(startDate, Calendar.DAY_OF_MONTH) : 1; day <= (i == months.size() - 1 ? get(endDate, Calendar.DAY_OF_MONTH) : toCalendar(months.get(i)).getActualMaximum(Calendar.DAY_OF_MONTH)); day++) {
				final Calendar calendar = Calendar.getInstance();
				calendar.set(get(months.get(i), Calendar.YEAR), get(months.get(i), Calendar.MONTH), day);
				
				days.add(setLastForDay(calendar.getTime()));
			}
		}
		
		return days;
	}
	
	public static Long differenceInMilliseconds(final Date endDate, final Date startDate) {
		return endDate.getTime() - startDate.getTime();
	}
	
	public static Long differenceInSeconds(final Date endDate, final Date startDate) {
		return differenceInMilliseconds(endDate, startDate) / 1000;
	}
	
	public static Long differenceInMinutes(final Date endDate, final Date startDate) {
		return differenceInSeconds(endDate, startDate) / 60;
	}
	
	public static Long differenceInHours(final Date endDate, final Date startDate) {
		return differenceInMinutes(endDate, startDate) / 60;
	}
}
