#! /usr/bin/env python
# coding: utf-8
# -*- coding: utf_8 -*-


import sqlite3
import re
import unicodedata
import sys


# def remove_vowels(arabic_text_with_vowels):
#     vowels = u"[\u064B-\u065F]"  # vowel character range
#     arabic_text = re.sub(vowels, u'', arabic_text_with_vowels)
#     return arabic_text


def remove_database_records(sqlite_name):
    conn = sqlite3.connect(sqlite_name)
    cur = conn.cursor()
    cur.execute('delete from pages')
    conn.commit()
    conn.close
    print ("All records are removed from " +sqlite_name)




def strip_diacritics(text):
    import unicodedata
    # return ''.join([c for c in unicodedata.normalize('NFD', text) \
    # if unicodedata.category(c) != 'Mn'])
    if text and len(text) > 0:
        return ''.join([c for c in text if unicodedata.category(c) != 'Mn'])
    else:
        return ''


# Note: make file name start with 001, up to 114 to ensure test results are sorted properly.


file_names = ["Introduction.txt",
              "18-Kahf.txt",
              "50-Qaf.txt",
              "54-Qamar.txt",
              "112-Elekhlas.txt",
              "References.html"]


# file_names = [
#
#
#
#               "112-Elekhlas.txt"
#               ]



# text_file_name = "18-Kahf.txt"
sqlite_name = 'books.sqlite'

print ("import text files and insert records into sqlite file")

remove_database_records(sqlite_name)

conn = sqlite3.connect(sqlite_name)
cur = conn.cursor()

page_id = 0
book_index = 1
# book_code_prefix = u"soura"

for text_file_name in file_names:
    print "\nfile name:", text_file_name
    parent_id =""
    title =u""
    page =u""
    page_fts =u""
    # book_code =  book_code_prefix + str(book_index)
    book_code = text_file_name.replace(".", "_").replace("-", "_")
    print "working on file", book_code
    book_index += 1
    record =u""
    line = u""
    stack = []
    stack.append("NO_PARENT")
    current_header = u"H1"

    with open(text_file_name, 'rU') as file:
        file.readlines
        for line in file:
            line = line.strip()
            if len(line) > 0 :
                # print "line is[", line, "]"
                if line.find("H") == -1 : # NOT FOUND

                    if(text_file_name.endswith(".txt")):
                        record += line.decode("utf-8") + "\r\n<br>"
                    else:
                        record += line.decode("utf-8") + "\r\n"
                else:
                    # print "line is [", line, "]"
                    # handle stack of parent ids
                    line = line.strip()
                    #split lines to extract first line as title
                    if(len(record) > 0) : # if empty, just skip it
                        # print "[", record, "]"
                        lines = record.splitlines() #split on new line
                        # print "lines count is", len(lines)
                        title = lines[0]
                        # print "title is:", title
                        lines[0] = ""
                        joinedData = ""
                        for single_line in lines:
                            joinedData += " " + single_line

                        record_fts = strip_diacritics(unicode(joinedData))
                        parent_id = stack[len(stack)-1]
                        topic = (page_id, parent_id, book_code, title, joinedData, record_fts)
                        cur.execute(u'insert into pages (page_id, parent_id, book_code, title, page, page_fts) Values (?, ?, ?, ?, ?, ?)', topic)
                        record = "" # for the new line processing
                        # print page_id
                        # print record
                        sys.stdout.write('.')
                        sys.stdout.flush()
                        #handle parent id
                        if line.strip() > current_header:
                            print "stack append", line, current_header
                            stack.append(page_id)
                            current_header = line #update current line
                        elif line.strip() < current_header:
                            current_header = stack.pop()
                            print "stack pop"

                        page_id += 1


    # Last record handling
    # if len(page) > 0:
        # page_fts = remove_vowels(record)
        # topic = (page_id, parent_id, book_code, "", "", "")
        # cur.execute('insert into pages (page_id, parent_id, book_code, title, page, page_fts) Values (?, ?, ?, ?, ?, ?)', topic)


conn.commit()
conn.close()  # call basic function

print " "
print "Conversion completed for records counted:" + str(page_id)
