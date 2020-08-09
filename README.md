# CHANGELOG
1. Added support for FLOAT32 tflite model. 

# Real time image classification with React Native

Earlier attempts at Image classification over React Native involved sending image data to the model classifier by sending the image over the bridge or storing the image to disk and accessing the image on the native side. Here's an attempt at live image classification by processing from the camera feed on the native side and getting the output as a byte stream on the JS side using the react-native-camera-tflite library.

Huge shout-out to the people over at [react-native-camera](https://github.com/react-native-community/react-native-camera). This is essentially just a fork of their awesome work.

Note: This is currently developed only for Android but could be implemented for iOS. (See [here](https://github.com/jigsawxyz/react-native-coreml-image) for a CoreML implementation on iOS).

To start, let's create an empty react native project:

```
react-native init mobilenetapp
cd mobilenet-app
```

Let's add our dependencies:

```
npm i react-native-camera-tflite
```

Follow the install instructions (for android. Same as react-native-camera):

1. Insert the following lines inside the dependencies block in android/build.gradle:

```
    ...
    ext {
        buildToolsVersion = "29.0.2"
        minSdkVersion = 16
        compileSdkVersion = 29
        targetSdkVersion = 29
    }
```

2. Insert the following lines inside android/app/build.gradle

    android {
        ...
        aaptOptions {
            noCompress "tflite"
            noCompress "lite"
        }
    ...

Now let's use the download our model file from [here](https://github.com/aftadizca/react-native-camera-tflite/blob/master/model7.1_2020_05_26.tflite), and copy over the model7.1_2020_05_26.tflite file over to our project.

```
    mkdir -p ./android/app/src/main/assets
    cp model7.1_2020_05_26.tflite ./android/app/src/main/assets
```
Download or copy this file [here](https://raw.githubusercontent.com/aftadizca/Isyaratku/master/Output.json) and save as Output.js

Replace the content of App.js in your project root directory with the following:

```
    import React, {Component} from 'react';
import {StyleSheet, Text, View} from 'react-native';
import {RNCamera} from 'react-native-camera-tflite';
import outputs from './Output.json';
import _ from 'lodash';

let _currentInstant = 0;

export default class App extends Component {
  constructor(props) {
    super(props);
    this.state = {
      time: 0,
      output: '',
    };
  }

  processOutput({data}) {
    const probs = _.map(data, (item) => _.round(item, 2));
    const orderedData = _.chain(probs)
      .zip(outputs)
      .orderBy(0, 'desc')
      .map((item) => [item[0], item[1]])
      .value();
    const outputData = _.chain(orderedData)
      .take(1)
      .map((item) => `${item[1]}: ${item[0]}`)
      //.map((item) => `${item[1]}`)
      .join('\n')
      .value();

    //console.log(orderedData);
    const time = Date.now() - (_currentInstant || Date.now());
    const output = `${outputData}\nTime:${time} ms`;
    this.setState((state) => ({
      output,
    }));
    _currentInstant = Date.now();
  }

  render() {
    const modelParams = {
      file: 'model7.1_2020_05_26.tflite',
      inputDimX: 128,
      inputDimY: 128,
      outputDim: 5,
      freqms: 0,
    };
    return (
      <View style={styles.container}>
        <RNCamera
          ref={(ref) => {
            this.camera = ref;
          }}
          style={styles.preview}
          type={RNCamera.Constants.Type.back}
          flashMode={RNCamera.Constants.FlashMode.on}
          permissionDialogTitle={'Permission to use camera'}
          permissionDialogMessage={
            'We need your permission to use your camera phone'
          }
          onModelProcessed={(data) => this.processOutput(data)}
          modelParams={modelParams}>
          <Text style={styles.cameraText}>{this.state.output}</Text>
        </RNCamera>
      </View>
    );
  }
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    flexDirection: 'column',
    backgroundColor: 'black',
  },
  preview: {
    flex: 1,
    justifyContent: 'flex-end',
    alignItems: 'center',
    padding: 20,
  },
  cameraText: {
    color: 'white',
    fontSize: 30,
    fontWeight: 'bold',
    textAlign: 'center',
    alignItems: 'flex-end',
    justifyContent: 'flex-end',
  },
});
```

We're done! Run your app with the following command.

```
    react-native run-android
```

Run your app with the following command.

```
    react-native run-android
```
![WAYANG KULIT](https://i.ibb.co/wzgt2YP/ezgif-com-optimize.gif)
*DEMO WAYANG KULIT*

This project has a lot of rough edges. I hope to clean up this up a lot more in the coming days. The rest of the features are the same as `react-native-camera`.

Links:
[Github](https://github.com/ppsreejith/react-native-camera-tflite)
[Demo App](https://github.com/ppsreejith/tflite-demo)
[npm](https://www.npmjs.com/package/react-native-camera-tflite)
