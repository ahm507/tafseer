#! /usr/bin/env python
# coding: utf-8
# -*- coding: utf_8 -*-


import sqlite3
import re
import unicodedata
import sys


file_names = ["18-Kahf.txt", "50-Qaf.txt"]

for text_file_name in file_names:
    print "file name:", text_file_name


# my_stack = []
# my_stack.append("NO_PARENT")
# my_stack.append("0")
#
# print my_stack[len(my_stack)-1]
# print my_stack.pop()
# print my_stack


#
#
# current_header = "H2"
# last_header = "H1"
#
# if current_header > last_header:
#     print "Bigger"
# else:
#     print "Not"