'''
Created on 09-Feb-2015

@author: Sandeep Raikar
'''
import dropbox

def dropboxAuth():
        access_token='your_access_token'
        client = dropbox.client.DropboxClient(access_token)
        print 'linked account: ', client.account_info()
        return client
    
def uploadFile(filePath,fileName,dbxClient):
    f=open(filePath,'rb')
    status = dbxClient.put_file(fileName,f)
    print status
    print 'Upload Success!'
    return status

def download_signed_file(fileName,dbxClient):
    f, metadata = dbxClient.get_file_and_metadata('/'+fileName)
    out = open('D:/CloudTest/GNU_Sign/'+fileName, 'wb')
    out.write(f.read())
    out.close()
    print metadata
    print 'Download successful!'

def download_file(fileName,dbxClient):
    f, metadata = dbxClient.get_file_and_metadata('/'+fileName)
    out = open('D:/CloudTest/GNU_Decrypt/'+fileName, 'wb')
    out.write(f.read())
    out.close()
    print metadata
    print 'Download successful!'
