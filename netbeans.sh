#!/bin/sh
#netbeans=${NETBEANS:-"/Applications/NetBeans/NetBeans 8.2.app/Contents/Resources/NetBeans/bin/netbeans"}
netbeans=${NETBEANS:-"/Applications/NetBeans/netbeans9/bin/netbeans"}
mkdir .netbeans
"$netbeans" --userdir .netbeans