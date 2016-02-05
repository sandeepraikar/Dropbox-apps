'''
Created on 11-Feb-2015

@author: Sandeep Raikar
'''
import httplib2
import pprint
import os
from apiclient.discovery import build
from apiclient.http import MediaFileUpload
from oauth2client.client import OAuth2WebServerFlow


def gdrive_connect():
    
    # Copy your credentials from the console
    CLIENT_ID = 'your_client_id'
    CLIENT_SECRET = 'your_client_secret_key'
    
    # Check https://developers.google.com/drive/scopes for all available scopes
    OAUTH_SCOPE = 'https://www.googleapis.com/auth/drive'
    
    # Redirect URI for installed apps
    REDIRECT_URI = 'urn:ietf:wg:oauth:2.0:oob'

    # Run through the OAuth flow and retrieve credentials
    flow = OAuth2WebServerFlow(CLIENT_ID, CLIENT_SECRET, OAUTH_SCOPE,
                               redirect_uri=REDIRECT_URI)
    authorize_url = flow.step1_get_authorize_url()
    print 'Go to the following link in your browser: ' + authorize_url
    code = raw_input('Enter verification code: ').strip()
    credentials = flow.step2_exchange(code)
    
    # Create an httplib2.Http object and authorize it with our credentials
    http = httplib2.Http()
    http = credentials.authorize(http)
    
    drive_service = build('drive', 'v2', http=http)
    return drive_service

def upload_file_gdrive(file_path,file_name,gdrive_client):
    media_body = MediaFileUpload(file_path, mimetype='text/plain', resumable=True)
    
    body = {
      'title': os.path.splitext(file_name)[0],
    }
    
    status = gdrive_client.files().insert(body=body,media_body=media_body).execute()
    pprint.pprint(status)
    print 'File : '+file_name+ '  uploaded successfully in Google Drive'
    