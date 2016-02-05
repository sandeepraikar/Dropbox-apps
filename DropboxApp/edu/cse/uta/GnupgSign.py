'''
Created on 10-Feb-2015

@author: Sandeep Raikar
'''
import gnupg
import time
from shutil import copy

gpg = gnupg.GPG()
gpg.encoding = 'utf-8'

def sign_file(filePath,fileName):
    copy(filePath,'D:/CloudTest/GNU_Sign/')
    time.sleep(1)
    stream = open('D:/CloudTest/GNU_Sign/'+fileName,'rb')
    signed_data = gpg.sign_file(stream, keyid='9B3350AB', detach=True)
    print signed_data
   
    f=open('D:/CloudTest/GNU_Sign/'+fileName+'.sign','wb')
    f.write(str(signed_data))
    f.close()
    
def verify_signed_file(fileName):
    print 'D:/CloudTest/GNU_Sign/'+fileName+'.sign'
    print 'D:/CloudTest/GNU_Sign/'+fileName
    stream = open('D:/CloudTest/GNU_Sign/'+fileName+'.sign','rb')
    verified = gpg.verify_file(stream, 'D:/CloudTest/GNU_Sign/'+fileName)
    print 'Verified' if verified else 'Unverified'