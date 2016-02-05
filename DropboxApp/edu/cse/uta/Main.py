'''
Created on 09-Feb-2015

@author: Sandeep
'''
import time
import sys
import os
from watchdog.observers import Observer
from watchdog.events import PatternMatchingEventHandler
from RSACryptor import encryptFile
from DboxConnect import dropboxAuth
from edu.cse.uta.DboxConnect import uploadFile, download_signed_file, download_file
from edu.cse.uta.GDriveConnect import gdrive_connect, upload_file_gdrive
from edu.cse.uta.GnupgSign import sign_file, verify_signed_file
from edu.cse.uta.RSACryptor import decryptFile


global dboxClient, g_drive_client
dboxClient = dropboxAuth()
#g_drive_client = gdrive_connect()

mode = raw_input("Press 'S' for sign and Upload and 'E' for Encrypt and Upload: ").strip()
            
print 'Please drop files in D:/CloudTest/GNU'

class UploadFolderWatchHandler(PatternMatchingEventHandler):
    patterns = ["*.txt", "*.xml", "*.xlsx" , "*.pdf" , "*.docx" , "*.jpeg", "*.*"]

    def process(self, event):
        print(event.src_path, event.event_type)
      
        if(event.event_type == 'created'):
            filePath= event.src_path
            print filePath
            fileName= os.path.basename(filePath) 
            print 'File dropped in the Upload path : '+fileName

            if mode=='E':
                encryptedFilePath = encryptFile(filePath,fileName)
                print encryptedFilePath
            
                print 'Encryption successful'
                print 'Starting to upload the file to Dropbox!'
            
                uploadFile(encryptedFilePath,fileName,dboxClient)
                print 'File : '+fileName+ ' Uploaded Successfully!'
                
                time.sleep(1)
                download_file(fileName,dboxClient)
                time.sleep(1)
                decryptFile(fileName)
                
                ##Upload files to GoogleDrive
                #upload_file_gdrive(encryptedFilePath,fileName,g_drive_client)
                
                
                
            elif mode=='S':
                sign_file(filePath,fileName)
                 
#                 uploadFile('D:/CloudTest/GNU_Sign/'+fileName, fileName, dboxClient)
#                 uploadFile('D:/CloudTest/GNU_Sign/'+fileName+'.sign', fileName+'.sign', dboxClient)
#                  
#                 time.sleep(1)
#                  
#                 download_signed_file(fileName,dboxClient)
#                 time.sleep(1)
#                 download_signed_file(fileName+'.sign',dboxClient)
#                  
#                 verify_signed_file(fileName)
#                  
                
    def on_created(self, event):
        self.process(event)

if __name__ == '__main__':
    args = sys.argv[1:]
    path = 'D:\CloudTest\GNU'            
    event_handler = UploadFolderWatchHandler()
    observer = Observer()
    observer.schedule(event_handler, path ,recursive=False)
    observer.start()

    try:
        while True:
            time.sleep(5)
    except KeyboardInterrupt:
        observer.stop()
    observer.join()

