# General remarks #
It seems that the creators of the pdr file format stores the memory structures very straightforward. The description that follows was manually reverse engineered from several test files, produced with the Amazon Kindle 3.

# Data types #
In the following description I will use the following datatypes:
  * u8  _byte_
  * u16 _short_
  * u32 _int_
  * string A length prefixed string. First a u16 containing the length and then an array of chars.
  * double A _double_

To read the data a [DataInputStream](http://download.oracle.com/javase/6/docs/api/java/io/DataInputStream.html) is used. I was also able to read the data with the help of the python [struct](http://docs.python.org/library/struct.html). module

# File format details #
The file format is described in some kind of c structs. All the bytes I am not able to understand are skipped with three dots.

```
struct pdr {
  u32     header;
  u8       ...;
  u32      last_displayed_page;
  u32      number_of_bookmarks;
  bookmark bookmarks[number_of_bookmarks];
  u8       ...[20];
  u32      number_of_markings;
  marking  markings[number_of_markings];
  u32      number_of_comments;
  comment  comments[number_of_comments];
  u8       ...[4]; // values seen so far: 2 and 3
};
```

The file starts with a magic value that identify the format and is always **0xDEADCABB**. The different kinds of annotations are stored in arrays that are preceded by the number of entries.

```
struct bookmark {
  u8      type; // always 0x00
  u32     page;
  string  page_name;
};
```
A bookmark is a marking of a certain page.

```
struct marking {
  u8      type; // always 0x01
  u32     page1;
  string  page_name1;
  string  pdfloc1;
  u8      ...[4]; // perhaps a float
  double  x1;
  double  y1;

  u32     page2;
  string  page_name2;
  string  pdfloc2;
  u8      ...[4]; // perhaps a float
  double  x2;
  double  y2;
  u8      ...[2];
};
```
A marking contains a start position and an end position and some additional data.

```
struct comment {
  u8      type; // always 0x02
  u32     page;
  string  page_name;
  double  x;
  double  y;
  string  pdfloc;
  string  content;  
};
```

The comment annotation contains beside of the location information the text that was added.


# pdfloc #
In the beginning there was the idea that the pdfloc-text contains the necessary position information. But after several tests we had a situation where two different positions produced the same pdfloc.