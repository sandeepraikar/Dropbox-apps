'''
Created on 10-Feb-2015

@author: Sandeep
'''
import gnupg

gpg = gnupg.GPG()
gpg.encoding = 'utf-8'
# 
# input_data = gpg.gen_key_input(key_type="RSA", key_length=2048,name_email="dbox@mydomain.com")
# key = gpg.gen_key(input_data)
#  
# key_fingerprint = key.fingerprint  
# publickey = gpg.export_keys(key_fingerprint) 
# privatekey = gpg.export_keys(key_fingerprint,secret=True) 
#  
# #Exporting the keys
# with open('D:\CloudTest\KeyStore\mykeyfile.asc', 'w') as f:
#     f.write(publickey)
#     f.write(privatekey)

def encryptFile(filePath,fileName):
    inputfile = open(filePath,'rb')
    print 'Encrypting the file'
    status = gpg.encrypt_file(inputfile,recipients='sandeep.raikar@mavs.uta.edu', output='D:\CloudTest\GNU_Enc\\' + fileName)
    inputfile.close()
    print 'ok: ', status.ok
    print 'status: ', status.status
    encryptedFileLocation = 'D:\CloudTest\GNU_Enc\\'+fileName
    return encryptedFileLocation

def decryptFile(fileName):
    inputfile = open('D:/CloudTest/GNU_Decrypt/'+fileName,'rb')
    print 'DeCrypting the file'
    status = gpg.decrypt_file(inputfile, passphrase='cse6331quiz',output='D:\CloudTest\GNU_Download\\' + fileName)
    print 'ok: ', status.ok
    print 'status: ', status.status
    decryptedFileLocation = 'D:\CloudTest\GNU_Download\\'+fileName
    print decryptedFileLocation