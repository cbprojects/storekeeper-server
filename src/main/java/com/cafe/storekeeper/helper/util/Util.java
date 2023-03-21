package com.cafe.storekeeper.helper.util;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Query;

import com.cafe.storekeeper.helper.constant.ConstantsRest;

public final class Util {
    private Util() {
    }

    public static boolean isNumeric(String cadena) {
        return isInteger(cadena) || isDouble(cadena) || isLong(cadena);
    }

    public static boolean isDouble(String cadena) {
        boolean resultado;
        try {
            Double.parseDouble(cadena);
            resultado = true;
        } catch (NumberFormatException excepcion) {
            resultado = false;
        }
        return resultado;
    }

    public static boolean isLong(String cadena) {
        boolean resultado;
        try {
            Long.parseLong(cadena);
            resultado = true;
        } catch (NumberFormatException excepcion) {
            resultado = false;
        }
        return resultado;
    }

    public static boolean isInteger(String cadena) {
        boolean resultado;
        try {
            Integer.parseInt(cadena);
            resultado = true;
        } catch (NumberFormatException excepcion) {
            resultado = false;
        }
        return resultado;
    }

    public static Query paginateResults(Query query, Map<String, String> params) {
        if (query != null && params != null && !params.isEmpty()) {
            String count = params.get(ConstantsRest.COUNT);
            String skip = params.get(ConstantsRest.SKIP);

            boolean flagCount = StringUtils.isNotBlank(count) && Util.isNumeric(count);
            boolean flagSkip = StringUtils.isNotBlank(skip) && Util.isNumeric(skip);

            if (flagCount) {
                query.limit(Integer.parseInt(count));
            }
            if (flagSkip) {
                query.skip(Long.parseLong(skip));
            }
        }
        return query;
    }

    public static Query addSort(Query query, Map<String, String> params) {
        if (query == null) {
            query = new Query();
        }
        if (params != null && !params.isEmpty()) {
            String sort = params.get(ConstantsRest.SORT);
            boolean flagSort = StringUtils.isNotBlank(sort);

            if (flagSort) {
                String typeSort = params.get(ConstantsRest.TYPE_SORT);
                typeSort = StringUtils.isNotBlank(typeSort) ? typeSort : "ASC";
                Sort.Direction direction = Sort.Direction.ASC;
                if (typeSort.equals("DESC")) {
                    direction = Sort.Direction.DESC;
                }
                List<String> fieldsSort = Arrays.asList(sort.split(","));
                Sort sortQuery = Sort.by(direction, fieldsSort.toArray(new String[0]));
                query.with(sortQuery);
            }
        }
        return query;
    }

    public static String concatenate(List<String> listWords) {
        StringBuilder result = new StringBuilder();
        if (listWords != null && !listWords.isEmpty()) {
            listWords.forEach(result::append);
        }
        return result.toString();
    }

    public static boolean isDate(String dateString, DateTimeFormatter formatter) {
        try {
            LocalDate paymentDate = LocalDate.parse(dateString, formatter);
            return paymentDate != null;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    public static boolean isDateTime(String dateString, DateTimeFormatter formatter) {
        try {
            LocalDateTime paymentDate = LocalDateTime.parse(dateString, formatter);
            return paymentDate != null;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    public static <T> Boolean isPresentFirstElement(List<T> list) {
        return null != list && !list.isEmpty() && null != list.get(0);
    }

    public static String formatDateTime(String formatDate, LocalDateTime date) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(formatDate)
                    .withResolverStyle(ResolverStyle.STRICT);
            return formatter.format(date);
        } catch (Exception e) {
            return "";
        }
    }

    public static String formatDate(String formatDate, LocalDate date) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(formatDate)
                    .withResolverStyle(ResolverStyle.STRICT);
            return formatter.format(date);
        } catch (Exception e) {
            return "";
        }

    }

    public static String formatCurrencyBigDecimal(String currencySymbol, char groupingSeparator, char decimalSeparator,
            BigDecimal value) {
        try {
            NumberFormat numberFormat = NumberFormat.getInstance(Locale.US);
            numberFormat.setMinimumFractionDigits(2);
            numberFormat.setMaximumFractionDigits(2);
            DecimalFormatSymbols decimalFormat = getDecimalFormatSymbols(currencySymbol, groupingSeparator,
                    decimalSeparator);
            ((DecimalFormat) numberFormat).setDecimalFormatSymbols(decimalFormat);

            return numberFormat.format(value);
        } catch (Exception e) {
            return "";
        }
    }

    public static DecimalFormatSymbols getDecimalFormatSymbols(String currencySymbol, char groupingSeparator,
            char decimalSeparator) {
        try {
            DecimalFormatSymbols decimalFormat = new DecimalFormatSymbols();
            decimalFormat.setCurrencySymbol(currencySymbol);
            decimalFormat.setGroupingSeparator(groupingSeparator);
            decimalFormat.setMonetaryDecimalSeparator(decimalSeparator);
            decimalFormat.setDecimalSeparator(decimalSeparator);

            return decimalFormat;
        } catch (Exception e) {
            return null;
        }
    }

}
