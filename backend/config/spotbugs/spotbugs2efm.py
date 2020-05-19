#!/usr/bin/env python
import sys
import xml.etree.ElementTree as ET


def transform(contents):
    root = ET.fromstring(contents)

    project = root.findall('Project')[0]
    source_dir = project.findall('SrcDir')[1].text


    for child in root:
        if child.tag == 'BugInstance':
            type = child.get('type')
            message = child.findall("ShortMessage")[0].text
            source_line = child.findall("SourceLine")[0]
            begin_line = source_line.get('start')
            file_name = source_line.get('sourcepath')
            print(f"{source_dir}/{file_name}:{begin_line}: {type}({message})")


if __name__ == "__main__":
    transform(sys.stdin.read())
