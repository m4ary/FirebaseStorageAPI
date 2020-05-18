
# FirebaseStorageAPI [![](https://jitpack.io/v/mshlab/FirebaseStorageAPI.svg)](https://jitpack.io/#mshlab/FirebaseStorageAPI)

FirebaseStorageAPI is an Android Library wrapper for Firebase Storage functionality.
<img src="https://github.com/mshlab/FirebaseStorageAPI/blob/master/README/readme_demo.gif?raw=true" alt="screenshot" width="220" align="right">

## Features
- built-in Progress Dialog
  - customizable messages fit your need and language  
- Upload and download files to Firebase Storage Bucket in different forms like:
  -  stream as an IntputStream
  - Array of Bytes
  - File on Device Storage
- Delete files


<br>




## Download
### 1- Setup Firebase Storage 
Add Firebase Storage library to your Firebase project
[https://firebase.google.com/docs/storage/android/start](https://firebase.google.com/docs/storage/android/start)
### 2-Add Gradle dependency
- Add the following to your project level build.gradle:
~~~
allprojects {
	repositories {
		...
		maven { url "https://jitpack.io" }
	}
}
~~~
- Add this to your app build.gradle:
~~~
dependencies {
...
	 implementation 'com.github.mshlab:FirebaseStorageAPI:v1.0-release'
}

~~~
## Usage
1- create a FirebaseStorageAPI object
~~~
    FirebaseStorageAPI firebaseStorageAPI = new FirebaseStorageAPI.Builder()  
        .setVisibleAcitivty(this) //required  
        .build();
~~~


- add more custom options
~~~ 
    firebaseStorageAPI = new FirebaseStorageAPI.Builder()  
        .setVisibleAcitivty(this) //required  
		.setCancelMessage("file download canceled") 
		.setDownloadingMessage("downloading from the sky")
		.setErrorMessage("error in gating the file, try later") 
		.setUploadingMessage("carrying it to cloud")
		.setLoadingMessage("waiting to start")
		.allowCancel(true) //let the user choose to cancel the download  
		.build();
~~~

2- defined a storage reference (path where you will upload or download your file) [learn how](https://firebase.google.com/docs/storage/android/create-reference)
~~~
StorageReference mStorageRef = FirebaseStorage.getInstance().
getReference().
getRoot().
child("pics").
child("sky.png");
~~~
**A- Upload function**

1- prepare the data as :
  - inputstream
~~~
 InputStream DataToUpload = ...;  
~~~

  - File
~~~
 Uri DataToUpload = Uri.fromFile(new File("/sdcard/hello.txt"));
~~~

  - Bytes
~~~
  String string = "helloWorldInBytes";
  byte[] DataToUpload=string.getBytes();
~~~    
2- pass it to `upload` method
~~~
  firebaseStorageAPI.upload(DataToUpload, mStorageRef, new OnCompleteListener() {  
        @Override  
  public void onComplete(@NonNull Task task) {  
          if (task.isSuccessful()) {  
           mStorageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {  
           
            @Override  
			public void onSuccess(Uri ) {  
       statusTextView.setText("file uploaded successfully\n File URL: " + uri.toString());  
  }}); }} });  
~~~

**B- Download function**
  - InputStream
~~~
firebaseStorageAPI.downloadAsStream(mStorageRef, new OnCompleteListener<StreamDownloadTask.TaskSnapshot>() {  
    @Override  
  public void onComplete(@NonNull final Task<StreamDownloadTask.TaskSnapshot> task) {  
       
 if (task.isComplete()) {  
  InputStream is = task.getResult().getStream();  
   //InputStream is ready to use  
		}  
    }  
});
~~~

  - File
~~~
File localFile = File.createTempFile("images", "jpg");  
firebaseStorageAPI.downloadToLocalPath(mStorageRef, localFile, new OnCompleteListener<FileDownloadTask.TaskSnapshot>() {  
    @Override  
  public void onComplete(@NonNull Task<FileDownloadTask.TaskSnapshot> task) {  
        if (task.isComplete()) {  
        // download is complete, do your thing with the localFile ... 
            } 
         }
      });
~~~
  - Bytes
~~~
firebaseStorageAPI.downloadAsBytes(mStorageRef, Long.MAX_VALUE, new OnCompleteListener<byte[]>({  
    @Override  
  public void onComplete(@NonNull Task<byte[]> task) {  
 if (task.isComplete()) {
    byte[] dataInBytes = task.getResult();  
	// download complete , do your thing with the dataInBytes
  } 
 }  
});
~~~






## Buy me a coffee
<a href="https://www.buymeacoffee.com/mshlab" target="_blank"><img src="https://cdn.buymeacoffee.com/buttons/lato-orange.png" alt="Buy Me A Coffee" style="height: 51px !important;width: 217px !important;" ></a> 
### License
~~~
    Apache Version 2.0

    Copyright 2016.

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
~~~
