#!/bin/sh

#create beginning and ending parts of html template
htmlTop='<!DOCTYPE html><html><head><title>Directories</title><meta charset="utf-8"></head><p>'
htmlBottom="</p></html>"

#save html break tag in variable
breakTag="<br>"

#iterate through each file/directory name in directory
for item in *
do
    #concatenate html break tag to end of name, save to newItem
    newItem=$item$breakTag
    
    #concatenate newItem to end of beginning part of html template
    htmlTop=$htmlTop$newItem
done

#concatentate ending part of html template onto resulting html
rawHtml=$htmlTop$htmlBottom

#output raw html to new html file and open in broswer
echo "$rawHtml" > list.html
open list.html

