package org.apache.commons.csv;

import com.dlong.creeper.annotation.csv.CsvHeader;
import com.dlong.creeper.util.FieldUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Serializable;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.*;

public final class CSVFormat implements Serializable {
    public static final CSVFormat DEFAULT;
    public static final CSVFormat EXCEL;
    public static final CSVFormat INFORMIX_UNLOAD;
    public static final CSVFormat INFORMIX_UNLOAD_CSV;
    public static final CSVFormat MYSQL;
    public static final CSVFormat ORACLE;
    public static final CSVFormat POSTGRESQL_CSV;
    public static final CSVFormat POSTGRESQL_TEXT;
    public static final CSVFormat RFC4180;
    private static final long serialVersionUID = 1L;
    public static final CSVFormat TDF;
    private final boolean allowMissingColumnNames;
    private final Character commentMarker;
    private final char delimiter;
    private final Character escapeCharacter;
    private final String[] header;
    private final String[] headerComments;
    private final boolean ignoreEmptyLines;
    private final boolean ignoreHeaderCase;
    private final boolean ignoreSurroundingSpaces;
    private final String nullString;
    private final Character quoteCharacter;
    private final QuoteMode quoteMode;
    private final String recordSeparator;
    private final boolean skipHeaderRecord;
    private final boolean trailingDelimiter;
    private final boolean trim;
    private final boolean autoFlush;

    private static boolean isLineBreak(char c) {
        return c == '\n' || c == '\r';
    }

    private static boolean isLineBreak(Character c) {
        return c != null && isLineBreak(c.charValue());
    }

    public static CSVFormat newFormat(char delimiter) {
        return new CSVFormat(delimiter, (Character)null, (QuoteMode)null, (Character)null, (Character)null, false, false, (String)null, (String)null, (Object[])null, (String[])null, false, false, false, false, false, false);
    }

    public static CSVFormat valueOf(String format) {
        return CSVFormat.Predefined.valueOf(format).getFormat();
    }

    private CSVFormat(char delimiter, Character quoteChar, QuoteMode quoteMode, Character commentStart, Character escape, boolean ignoreSurroundingSpaces, boolean ignoreEmptyLines, String recordSeparator, String nullString, Object[] headerComments, String[] header, boolean skipHeaderRecord, boolean allowMissingColumnNames, boolean ignoreHeaderCase, boolean trim, boolean trailingDelimiter, boolean autoFlush) {
        this.delimiter = delimiter;
        this.quoteCharacter = quoteChar;
        this.quoteMode = quoteMode;
        this.commentMarker = commentStart;
        this.escapeCharacter = escape;
        this.ignoreSurroundingSpaces = ignoreSurroundingSpaces;
        this.allowMissingColumnNames = allowMissingColumnNames;
        this.ignoreEmptyLines = ignoreEmptyLines;
        this.recordSeparator = recordSeparator;
        this.nullString = nullString;
        this.headerComments = this.toStringArray(headerComments);
        this.header = header == null ? null : (String[])header.clone();
        this.skipHeaderRecord = skipHeaderRecord;
        this.ignoreHeaderCase = ignoreHeaderCase;
        this.trailingDelimiter = trailingDelimiter;
        this.trim = trim;
        this.autoFlush = autoFlush;
        this.validate();
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj == null) {
            return false;
        } else if (this.getClass() != obj.getClass()) {
            return false;
        } else {
            CSVFormat other = (CSVFormat)obj;
            if (this.delimiter != other.delimiter) {
                return false;
            } else if (this.quoteMode != other.quoteMode) {
                return false;
            } else {
                if (this.quoteCharacter == null) {
                    if (other.quoteCharacter != null) {
                        return false;
                    }
                } else if (!this.quoteCharacter.equals(other.quoteCharacter)) {
                    return false;
                }

                if (this.commentMarker == null) {
                    if (other.commentMarker != null) {
                        return false;
                    }
                } else if (!this.commentMarker.equals(other.commentMarker)) {
                    return false;
                }

                if (this.escapeCharacter == null) {
                    if (other.escapeCharacter != null) {
                        return false;
                    }
                } else if (!this.escapeCharacter.equals(other.escapeCharacter)) {
                    return false;
                }

                if (this.nullString == null) {
                    if (other.nullString != null) {
                        return false;
                    }
                } else if (!this.nullString.equals(other.nullString)) {
                    return false;
                }

                if (!Arrays.equals(this.header, other.header)) {
                    return false;
                } else if (this.ignoreSurroundingSpaces != other.ignoreSurroundingSpaces) {
                    return false;
                } else if (this.ignoreEmptyLines != other.ignoreEmptyLines) {
                    return false;
                } else if (this.skipHeaderRecord != other.skipHeaderRecord) {
                    return false;
                } else {
                    if (this.recordSeparator == null) {
                        if (other.recordSeparator != null) {
                            return false;
                        }
                    } else if (!this.recordSeparator.equals(other.recordSeparator)) {
                        return false;
                    }

                    return true;
                }
            }
        }
    }

    public String format(Object... values) {
        StringWriter out = new StringWriter();

        try {
            CSVPrinter csvPrinter = new CSVPrinter(out, this);
            Throwable var4 = null;

            String var5;
            try {
                csvPrinter.printRecord(values);
                var5 = out.toString().trim();
            } catch (Throwable var15) {
                var4 = var15;
                throw var15;
            } finally {
                if (csvPrinter != null) {
                    if (var4 != null) {
                        try {
                            csvPrinter.close();
                        } catch (Throwable var14) {
                            var4.addSuppressed(var14);
                        }
                    } else {
                        csvPrinter.close();
                    }
                }

            }

            return var5;
        } catch (IOException var17) {
            throw new IllegalStateException(var17);
        }
    }

    public boolean getAllowMissingColumnNames() {
        return this.allowMissingColumnNames;
    }

    public boolean getAutoFlush() {
        return this.autoFlush;
    }

    public Character getCommentMarker() {
        return this.commentMarker;
    }

    public char getDelimiter() {
        return this.delimiter;
    }

    public Character getEscapeCharacter() {
        return this.escapeCharacter;
    }

    public String[] getHeader() {
        return this.header != null ? (String[])this.header.clone() : null;
    }

    public String[] getHeaderComments() {
        return this.headerComments != null ? (String[])this.headerComments.clone() : null;
    }

    public boolean getIgnoreEmptyLines() {
        return this.ignoreEmptyLines;
    }

    public boolean getIgnoreHeaderCase() {
        return this.ignoreHeaderCase;
    }

    public boolean getIgnoreSurroundingSpaces() {
        return this.ignoreSurroundingSpaces;
    }

    public String getNullString() {
        return this.nullString;
    }

    public Character getQuoteCharacter() {
        return this.quoteCharacter;
    }

    public QuoteMode getQuoteMode() {
        return this.quoteMode;
    }

    public String getRecordSeparator() {
        return this.recordSeparator;
    }

    public boolean getSkipHeaderRecord() {
        return this.skipHeaderRecord;
    }

    public boolean getTrailingDelimiter() {
        return this.trailingDelimiter;
    }

    public boolean getTrim() {
        return this.trim;
    }

    public int hashCode() {
        int prime = 1;
        int result = 1;
        result = 31 * result + this.delimiter;
        result = 31 * result + (this.quoteMode == null ? 0 : this.quoteMode.hashCode());
        result = 31 * result + (this.quoteCharacter == null ? 0 : this.quoteCharacter.hashCode());
        result = 31 * result + (this.commentMarker == null ? 0 : this.commentMarker.hashCode());
        result = 31 * result + (this.escapeCharacter == null ? 0 : this.escapeCharacter.hashCode());
        result = 31 * result + (this.nullString == null ? 0 : this.nullString.hashCode());
        result = 31 * result + (this.ignoreSurroundingSpaces ? 1231 : 1237);
        result = 31 * result + (this.ignoreHeaderCase ? 1231 : 1237);
        result = 31 * result + (this.ignoreEmptyLines ? 1231 : 1237);
        result = 31 * result + (this.skipHeaderRecord ? 1231 : 1237);
        result = 31 * result + (this.recordSeparator == null ? 0 : this.recordSeparator.hashCode());
        result = 31 * result + Arrays.hashCode(this.header);
        return result;
    }

    public boolean isCommentMarkerSet() {
        return this.commentMarker != null;
    }

    public boolean isEscapeCharacterSet() {
        return this.escapeCharacter != null;
    }

    public boolean isNullStringSet() {
        return this.nullString != null;
    }

    public boolean isQuoteCharacterSet() {
        return this.quoteCharacter != null;
    }

    public CSVParser parse(Reader in) throws IOException {
        return new CSVParser(in, this);
    }

    public CSVPrinter print(Appendable out) throws IOException {
        return new CSVPrinter(out, this);
    }

    public CSVPrinter print(File out, Charset charset) throws IOException {
        return new CSVPrinter(new OutputStreamWriter(new FileOutputStream(out), charset), this);
    }

    public void print(Object value, Appendable out, boolean newRecord) throws IOException {
        Object charSequence;
        if (value == null) {
            if (null == this.nullString) {
                charSequence = "";
            } else if (QuoteMode.ALL == this.quoteMode) {
                charSequence = this.quoteCharacter + this.nullString + this.quoteCharacter;
            } else {
                charSequence = this.nullString;
            }
        } else {
            charSequence = value instanceof CharSequence ? (CharSequence)value : value.toString();
        }

        charSequence = this.getTrim() ? this.trim((CharSequence)charSequence) : charSequence;
        this.print(value, (CharSequence)charSequence, 0, ((CharSequence)charSequence).length(), out, newRecord);
    }

    private void print(Object object, CharSequence value, int offset, int len, Appendable out, boolean newRecord) throws IOException {
        if (!newRecord) {
            out.append(this.getDelimiter());
        }

        if (object == null) {
            out.append(value);
        } else if (this.isQuoteCharacterSet()) {
            this.printAndQuote(object, value, offset, len, out, newRecord);
        } else if (this.isEscapeCharacterSet()) {
            this.printAndEscape(value, offset, len, out);
        } else {
            out.append(value, offset, offset + len);
        }

    }

    public CSVPrinter print(Path out, Charset charset) throws IOException {
        return this.print(Files.newBufferedWriter(out, charset));
    }

    private void printAndEscape(CharSequence value, int offset, int len, Appendable out) throws IOException {
        int start = offset;
        int pos = offset;
        int end = offset + len;
        char delim = this.getDelimiter();

        for(char escape = this.getEscapeCharacter().charValue(); pos < end; ++pos) {
            char c = value.charAt(pos);
            if (c == '\r' || c == '\n' || c == delim || c == escape) {
                if (pos > start) {
                    out.append(value, start, pos);
                }

                if (c == '\n') {
                    c = 'n';
                } else if (c == '\r') {
                    c = 'r';
                }

                out.append(escape);
                out.append(c);
                start = pos + 1;
            }
        }

        if (pos > start) {
            out.append(value, start, pos);
        }

    }

    private void printAndQuote(Object object, CharSequence value, int offset, int len, Appendable out, boolean newRecord) throws IOException {
        boolean quote = false;
        int start = offset;
        int pos = offset;
        int end = offset + len;
        char delimChar = this.getDelimiter();
        char quoteChar = this.getQuoteCharacter().charValue();
        QuoteMode quoteModePolicy = this.getQuoteMode();
        if (quoteModePolicy == null) {
            quoteModePolicy = QuoteMode.MINIMAL;
        }

        char c;
        switch(quoteModePolicy) {
        case ALL:
        case ALL_NON_NULL:
            quote = true;
            break;
        case NON_NUMERIC:
            quote = !(object instanceof Number);
            break;
        case NONE:
            this.printAndEscape(value, offset, len, out);
            return;
        case MINIMAL:
            if (len <= 0) {
                if (newRecord) {
                    quote = true;
                }
            } else {
                c = value.charAt(offset);
                if (c <= '#') {
                    quote = true;
                } else {
                    while(pos < end) {
                        c = value.charAt(pos);
                        if (c == '\n' || c == '\r' || c == quoteChar || c == delimChar) {
                            quote = true;
                            break;
                        }

                        ++pos;
                    }

                    if (!quote) {
                        pos = end - 1;
                        c = value.charAt(pos);
                        if (c <= ' ') {
                            quote = true;
                        }
                    }
                }
            }

            if (!quote) {
                out.append(value, offset, end);
                return;
            }
            break;
        default:
            throw new IllegalStateException("Unexpected Quote value: " + quoteModePolicy);
        }

        if (!quote) {
            out.append(value, offset, end);
        } else {
            out.append(quoteChar);

            for(; pos < end; ++pos) {
                c = value.charAt(pos);
                if (c == quoteChar) {
                    out.append(value, start, pos + 1);
                    start = pos;
                }
            }

            out.append(value, start, pos);
            out.append(quoteChar);
        }
    }

    public CSVPrinter printer() throws IOException {
        return new CSVPrinter(System.out, this);
    }

    public void println(Appendable out) throws IOException {
        if (this.getTrailingDelimiter()) {
            out.append(this.getDelimiter());
        }

        if (this.recordSeparator != null) {
            out.append(this.recordSeparator);
        }

    }

    public void printRecord(Appendable out, Object... values) throws IOException {
        for(int i = 0; i < values.length; ++i) {
            this.print(values[i], out, i == 0);
        }

        this.println(out);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Delimiter=<").append(this.delimiter).append('>');
        if (this.isEscapeCharacterSet()) {
            sb.append(' ');
            sb.append("Escape=<").append(this.escapeCharacter).append('>');
        }

        if (this.isQuoteCharacterSet()) {
            sb.append(' ');
            sb.append("QuoteChar=<").append(this.quoteCharacter).append('>');
        }

        if (this.isCommentMarkerSet()) {
            sb.append(' ');
            sb.append("CommentStart=<").append(this.commentMarker).append('>');
        }

        if (this.isNullStringSet()) {
            sb.append(' ');
            sb.append("NullString=<").append(this.nullString).append('>');
        }

        if (this.recordSeparator != null) {
            sb.append(' ');
            sb.append("RecordSeparator=<").append(this.recordSeparator).append('>');
        }

        if (this.getIgnoreEmptyLines()) {
            sb.append(" EmptyLines:ignored");
        }

        if (this.getIgnoreSurroundingSpaces()) {
            sb.append(" SurroundingSpaces:ignored");
        }

        if (this.getIgnoreHeaderCase()) {
            sb.append(" IgnoreHeaderCase:ignored");
        }

        sb.append(" SkipHeaderRecord:").append(this.skipHeaderRecord);
        if (this.headerComments != null) {
            sb.append(' ');
            sb.append("HeaderComments:").append(Arrays.toString(this.headerComments));
        }

        if (this.header != null) {
            sb.append(' ');
            sb.append("Header:").append(Arrays.toString(this.header));
        }

        return sb.toString();
    }

    private String[] toStringArray(Object[] values) {
        if (values == null) {
            return null;
        } else {
            String[] strings = new String[values.length];

            for(int i = 0; i < values.length; ++i) {
                Object value = values[i];
                strings[i] = value == null ? null : value.toString();
            }

            return strings;
        }
    }

    private CharSequence trim(CharSequence charSequence) {
        if (charSequence instanceof String) {
            return ((String)charSequence).trim();
        } else {
            int count = charSequence.length();
            int len = count;

            int pos;
            for(pos = 0; pos < len && charSequence.charAt(pos) <= ' '; ++pos) {
                ;
            }

            while(pos < len && charSequence.charAt(len - 1) <= ' ') {
                --len;
            }

            return pos <= 0 && len >= count ? charSequence : charSequence.subSequence(pos, len);
        }
    }

    private void validate() throws IllegalArgumentException {
        if (isLineBreak(this.delimiter)) {
            throw new IllegalArgumentException("The delimiter cannot be a line break");
        } else if (this.quoteCharacter != null && this.delimiter == this.quoteCharacter.charValue()) {
            throw new IllegalArgumentException("The quoteChar character and the delimiter cannot be the same ('" + this.quoteCharacter + "')");
        } else if (this.escapeCharacter != null && this.delimiter == this.escapeCharacter.charValue()) {
            throw new IllegalArgumentException("The escape character and the delimiter cannot be the same ('" + this.escapeCharacter + "')");
        } else if (this.commentMarker != null && this.delimiter == this.commentMarker.charValue()) {
            throw new IllegalArgumentException("The comment start character and the delimiter cannot be the same ('" + this.commentMarker + "')");
        } else if (this.quoteCharacter != null && this.quoteCharacter.equals(this.commentMarker)) {
            throw new IllegalArgumentException("The comment start character and the quoteChar cannot be the same ('" + this.commentMarker + "')");
        } else if (this.escapeCharacter != null && this.escapeCharacter.equals(this.commentMarker)) {
            throw new IllegalArgumentException("The comment start and the escape character cannot be the same ('" + this.commentMarker + "')");
        } else if (this.escapeCharacter == null && this.quoteMode == QuoteMode.NONE) {
            throw new IllegalArgumentException("No quotes mode set but no escape character is set");
        } else {
            if (this.header != null) {
                Set<String> dupCheck = new HashSet();
                String[] var2 = this.header;
                int var3 = var2.length;

                for(int var4 = 0; var4 < var3; ++var4) {
                    String hdr = var2[var4];
                    if (!dupCheck.add(hdr)) {
                        throw new IllegalArgumentException("The header contains a duplicate entry: '" + hdr + "' in " + Arrays.toString(this.header));
                    }
                }
            }

        }
    }

    public CSVFormat withAllowMissingColumnNames() {
        return this.withAllowMissingColumnNames(true);
    }

    public CSVFormat withAllowMissingColumnNames(boolean allowMissingColumnNames) {
        return new CSVFormat(this.delimiter, this.quoteCharacter, this.quoteMode, this.commentMarker, this.escapeCharacter, this.ignoreSurroundingSpaces, this.ignoreEmptyLines, this.recordSeparator, this.nullString, this.headerComments, this.header, this.skipHeaderRecord, allowMissingColumnNames, this.ignoreHeaderCase, this.trim, this.trailingDelimiter, this.autoFlush);
    }

    public CSVFormat withAutoFlush(boolean autoFlush) {
        return new CSVFormat(this.delimiter, this.quoteCharacter, this.quoteMode, this.commentMarker, this.escapeCharacter, this.ignoreSurroundingSpaces, this.ignoreEmptyLines, this.recordSeparator, this.nullString, this.headerComments, this.header, this.skipHeaderRecord, this.allowMissingColumnNames, this.ignoreHeaderCase, this.trim, this.trailingDelimiter, autoFlush);
    }

    public CSVFormat withCommentMarker(char commentMarker) {
        return this.withCommentMarker(commentMarker);
    }

    public CSVFormat withCommentMarker(Character commentMarker) {
        if (isLineBreak(commentMarker)) {
            throw new IllegalArgumentException("The comment start marker character cannot be a line break");
        } else {
            return new CSVFormat(this.delimiter, this.quoteCharacter, this.quoteMode, commentMarker, this.escapeCharacter, this.ignoreSurroundingSpaces, this.ignoreEmptyLines, this.recordSeparator, this.nullString, this.headerComments, this.header, this.skipHeaderRecord, this.allowMissingColumnNames, this.ignoreHeaderCase, this.trim, this.trailingDelimiter, this.autoFlush);
        }
    }

    public CSVFormat withDelimiter(char delimiter) {
        if (isLineBreak(delimiter)) {
            throw new IllegalArgumentException("The delimiter cannot be a line break");
        } else {
            return new CSVFormat(delimiter, this.quoteCharacter, this.quoteMode, this.commentMarker, this.escapeCharacter, this.ignoreSurroundingSpaces, this.ignoreEmptyLines, this.recordSeparator, this.nullString, this.headerComments, this.header, this.skipHeaderRecord, this.allowMissingColumnNames, this.ignoreHeaderCase, this.trim, this.trailingDelimiter, this.autoFlush);
        }
    }

    public CSVFormat withEscape(Character escape) {
        if (isLineBreak(escape)) {
            throw new IllegalArgumentException("The escape character cannot be a line break");
        } else {
            return new CSVFormat(this.delimiter, this.quoteCharacter, this.quoteMode, this.commentMarker, escape, this.ignoreSurroundingSpaces, this.ignoreEmptyLines, this.recordSeparator, this.nullString, this.headerComments, this.header, this.skipHeaderRecord, this.allowMissingColumnNames, this.ignoreHeaderCase, this.trim, this.trailingDelimiter, this.autoFlush);
        }
    }

    public CSVFormat withFirstRecordAsHeader() {
        return this.withHeader().withSkipHeaderRecord();
    }

    public CSVFormat withHeader(Class<?> headerClass) {
        String[] header = null;
        if (headerClass != null) {
            List<Field> fields = FieldUtil.getDeclaredFieldsWithParent(headerClass);
            int size = fields.size();
            header = new String[size];
            for(int i = 0; i < size; ++i) {
                Field field = fields.get(i);
                String name;
                int order = i;
                if(field.isAnnotationPresent(CsvHeader.class)){
                    CsvHeader csvHeader = field.getAnnotation(CsvHeader.class);
                    name = csvHeader.value();
                    order = csvHeader.order()-1;
                }else{
                    name = fields.get(i).getName();
                }
                if(header[order] == null){
                    header[order] = name;
                }
            }
        }
        return this.withHeader(header);
    }

    public CSVFormat withHeader(ResultSet resultSet) throws SQLException {
        return this.withHeader(resultSet != null ? resultSet.getMetaData() : null);
    }

    public CSVFormat withHeader(ResultSetMetaData metaData) throws SQLException {
        String[] labels = null;
        if (metaData != null) {
            int columnCount = metaData.getColumnCount();
            labels = new String[columnCount];

            for(int i = 0; i < columnCount; ++i) {
                labels[i] = metaData.getColumnLabel(i + 1);
            }
        }

        return this.withHeader(labels);
    }

    public CSVFormat withHeader(String... header) {
        return new CSVFormat(this.delimiter, this.quoteCharacter, this.quoteMode, this.commentMarker, this.escapeCharacter, this.ignoreSurroundingSpaces, this.ignoreEmptyLines, this.recordSeparator, this.nullString, this.headerComments, header, this.skipHeaderRecord, this.allowMissingColumnNames, this.ignoreHeaderCase, this.trim, this.trailingDelimiter, this.autoFlush);
    }

    public CSVFormat withHeaderComments(Object... headerComments) {
        return new CSVFormat(this.delimiter, this.quoteCharacter, this.quoteMode, this.commentMarker, this.escapeCharacter, this.ignoreSurroundingSpaces, this.ignoreEmptyLines, this.recordSeparator, this.nullString, headerComments, this.header, this.skipHeaderRecord, this.allowMissingColumnNames, this.ignoreHeaderCase, this.trim, this.trailingDelimiter, this.autoFlush);
    }

    public CSVFormat withIgnoreEmptyLines() {
        return this.withIgnoreEmptyLines(true);
    }

    public CSVFormat withIgnoreEmptyLines(boolean ignoreEmptyLines) {
        return new CSVFormat(this.delimiter, this.quoteCharacter, this.quoteMode, this.commentMarker, this.escapeCharacter, this.ignoreSurroundingSpaces, ignoreEmptyLines, this.recordSeparator, this.nullString, this.headerComments, this.header, this.skipHeaderRecord, this.allowMissingColumnNames, this.ignoreHeaderCase, this.trim, this.trailingDelimiter, this.autoFlush);
    }

    public CSVFormat withIgnoreHeaderCase() {
        return this.withIgnoreHeaderCase(true);
    }

    public CSVFormat withIgnoreHeaderCase(boolean ignoreHeaderCase) {
        return new CSVFormat(this.delimiter, this.quoteCharacter, this.quoteMode, this.commentMarker, this.escapeCharacter, this.ignoreSurroundingSpaces, this.ignoreEmptyLines, this.recordSeparator, this.nullString, this.headerComments, this.header, this.skipHeaderRecord, this.allowMissingColumnNames, ignoreHeaderCase, this.trim, this.trailingDelimiter, this.autoFlush);
    }

    public CSVFormat withIgnoreSurroundingSpaces() {
        return this.withIgnoreSurroundingSpaces(true);
    }

    public CSVFormat withIgnoreSurroundingSpaces(boolean ignoreSurroundingSpaces) {
        return new CSVFormat(this.delimiter, this.quoteCharacter, this.quoteMode, this.commentMarker, this.escapeCharacter, ignoreSurroundingSpaces, this.ignoreEmptyLines, this.recordSeparator, this.nullString, this.headerComments, this.header, this.skipHeaderRecord, this.allowMissingColumnNames, this.ignoreHeaderCase, this.trim, this.trailingDelimiter, this.autoFlush);
    }

    public CSVFormat withNullString(String nullString) {
        return new CSVFormat(this.delimiter, this.quoteCharacter, this.quoteMode, this.commentMarker, this.escapeCharacter, this.ignoreSurroundingSpaces, this.ignoreEmptyLines, this.recordSeparator, nullString, this.headerComments, this.header, this.skipHeaderRecord, this.allowMissingColumnNames, this.ignoreHeaderCase, this.trim, this.trailingDelimiter, this.autoFlush);
    }

    public CSVFormat withQuote(char quoteChar) {
        return this.withQuote(quoteChar);
    }

    public CSVFormat withQuote(Character quoteChar) {
        if (isLineBreak(quoteChar)) {
            throw new IllegalArgumentException("The quoteChar cannot be a line break");
        } else {
            return new CSVFormat(this.delimiter, quoteChar, this.quoteMode, this.commentMarker, this.escapeCharacter, this.ignoreSurroundingSpaces, this.ignoreEmptyLines, this.recordSeparator, this.nullString, this.headerComments, this.header, this.skipHeaderRecord, this.allowMissingColumnNames, this.ignoreHeaderCase, this.trim, this.trailingDelimiter, this.autoFlush);
        }
    }

    public CSVFormat withQuoteMode(QuoteMode quoteModePolicy) {
        return new CSVFormat(this.delimiter, this.quoteCharacter, quoteModePolicy, this.commentMarker, this.escapeCharacter, this.ignoreSurroundingSpaces, this.ignoreEmptyLines, this.recordSeparator, this.nullString, this.headerComments, this.header, this.skipHeaderRecord, this.allowMissingColumnNames, this.ignoreHeaderCase, this.trim, this.trailingDelimiter, this.autoFlush);
    }

    public CSVFormat withRecordSeparator(char recordSeparator) {
        return this.withRecordSeparator(String.valueOf(recordSeparator));
    }

    public CSVFormat withRecordSeparator(String recordSeparator) {
        return new CSVFormat(this.delimiter, this.quoteCharacter, this.quoteMode, this.commentMarker, this.escapeCharacter, this.ignoreSurroundingSpaces, this.ignoreEmptyLines, recordSeparator, this.nullString, this.headerComments, this.header, this.skipHeaderRecord, this.allowMissingColumnNames, this.ignoreHeaderCase, this.trim, this.trailingDelimiter, this.autoFlush);
    }

    public CSVFormat withSkipHeaderRecord() {
        return this.withSkipHeaderRecord(true);
    }

    public CSVFormat withSkipHeaderRecord(boolean skipHeaderRecord) {
        return new CSVFormat(this.delimiter, this.quoteCharacter, this.quoteMode, this.commentMarker, this.escapeCharacter, this.ignoreSurroundingSpaces, this.ignoreEmptyLines, this.recordSeparator, this.nullString, this.headerComments, this.header, skipHeaderRecord, this.allowMissingColumnNames, this.ignoreHeaderCase, this.trim, this.trailingDelimiter, this.autoFlush);
    }

    public CSVFormat withSystemRecordSeparator() {
        return this.withRecordSeparator(System.getProperty("line.separator"));
    }

    public CSVFormat withTrailingDelimiter() {
        return this.withTrailingDelimiter(true);
    }

    public CSVFormat withTrailingDelimiter(boolean trailingDelimiter) {
        return new CSVFormat(this.delimiter, this.quoteCharacter, this.quoteMode, this.commentMarker, this.escapeCharacter, this.ignoreSurroundingSpaces, this.ignoreEmptyLines, this.recordSeparator, this.nullString, this.headerComments, this.header, this.skipHeaderRecord, this.allowMissingColumnNames, this.ignoreHeaderCase, this.trim, trailingDelimiter, this.autoFlush);
    }

    public CSVFormat withTrim() {
        return this.withTrim(true);
    }

    public CSVFormat withTrim(boolean trim) {
        return new CSVFormat(this.delimiter, this.quoteCharacter, this.quoteMode, this.commentMarker, this.escapeCharacter, this.ignoreSurroundingSpaces, this.ignoreEmptyLines, this.recordSeparator, this.nullString, this.headerComments, this.header, this.skipHeaderRecord, this.allowMissingColumnNames, this.ignoreHeaderCase, trim, this.trailingDelimiter, this.autoFlush);
    }

    static {
        DEFAULT = new CSVFormat(',', Constants.DOUBLE_QUOTE_CHAR, (QuoteMode)null, (Character)null, (Character)null, false, true, "\r\n", (String)null, (Object[])null, (String[])null, false, false, false, false, false, false);
        EXCEL = DEFAULT.withIgnoreEmptyLines(false).withAllowMissingColumnNames();
        INFORMIX_UNLOAD = DEFAULT.withDelimiter('|').withEscape('\\').withQuote(Constants.DOUBLE_QUOTE_CHAR).withRecordSeparator('\n');
        INFORMIX_UNLOAD_CSV = DEFAULT.withDelimiter(',').withQuote(Constants.DOUBLE_QUOTE_CHAR).withRecordSeparator('\n');
        MYSQL = DEFAULT.withDelimiter('\t').withEscape('\\').withIgnoreEmptyLines(false).withQuote((Character)null).withRecordSeparator('\n').withNullString("\\N").withQuoteMode(QuoteMode.ALL_NON_NULL);
        ORACLE = DEFAULT.withDelimiter(',').withEscape('\\').withIgnoreEmptyLines(false).withQuote(Constants.DOUBLE_QUOTE_CHAR).withNullString("\\N").withTrim().withSystemRecordSeparator().withQuoteMode(QuoteMode.MINIMAL);
        POSTGRESQL_CSV = DEFAULT.withDelimiter(',').withEscape(Constants.DOUBLE_QUOTE_CHAR).withIgnoreEmptyLines(false).withQuote(Constants.DOUBLE_QUOTE_CHAR).withRecordSeparator('\n').withNullString("").withQuoteMode(QuoteMode.ALL_NON_NULL);
        POSTGRESQL_TEXT = DEFAULT.withDelimiter('\t').withEscape(Constants.DOUBLE_QUOTE_CHAR).withIgnoreEmptyLines(false).withQuote(Constants.DOUBLE_QUOTE_CHAR).withRecordSeparator('\n').withNullString("\\N").withQuoteMode(QuoteMode.ALL_NON_NULL);
        RFC4180 = DEFAULT.withIgnoreEmptyLines(false);
        TDF = DEFAULT.withDelimiter('\t').withIgnoreSurroundingSpaces();
    }

    public static enum Predefined {
        Default(CSVFormat.DEFAULT),
        Excel(CSVFormat.EXCEL),
        InformixUnload(CSVFormat.INFORMIX_UNLOAD),
        InformixUnloadCsv(CSVFormat.INFORMIX_UNLOAD_CSV),
        MySQL(CSVFormat.MYSQL),
        Oracle(CSVFormat.ORACLE),
        PostgreSQLCsv(CSVFormat.POSTGRESQL_CSV),
        PostgreSQLText(CSVFormat.POSTGRESQL_TEXT),
        RFC4180(CSVFormat.RFC4180),
        TDF(CSVFormat.TDF);

        private final CSVFormat format;

        private Predefined(CSVFormat format) {
            this.format = format;
        }

        public CSVFormat getFormat() {
            return this.format;
        }
    }
}
