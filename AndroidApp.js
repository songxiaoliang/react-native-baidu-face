/**
 * 人脸样本采集封装（百度AI-SDK）
 */
import React, {Component} from 'react';
import {
    StyleSheet,
    Text,
    View,
    Image,
    NativeModules,
    NativeEventEmitter,
    ScrollView,
    Platform
} from 'react-native';

const FaceCheckHelper = NativeModules.PushFaceViewControllerModule;
const FaceCheckModules = Platform.select({
    android: ()=> NativeModules.PushFaceViewControllerModule,
    ios: ()=> NativeModules.RNIOSExportJsToReact
})();
const NativeModule = new NativeEventEmitter(FaceCheckModules);

export default class App extends Component {

    constructor(props) {
        super(props);
        this.state = {
            imagesArray : [] // 采集的人脸样本结果
        };
    }

    componentDidMount() {
        NativeModule.addListener('FaceCheckHelper', (data) => this.faceCheckCallback(data));
    }

    /**
     * 人脸检测结果
     */
    faceCheckCallback(data) {
        this.setState({
            text: Object.keys(data)
        })
        if (data.remindCode == 0){
            let imagesArray = [];
            let imagesName = Object.keys(data.images); // bestImage liveEye liveYaw liveMouth yawRight yawLeft pitchUp pitchDown
            imagesName.map((info,index) =>{
                let image = data.images[info]
                if (Platform.OS === 'ios' && info === 'bestImage' && typeof(image) !== 'string') {
                    image = data.images.bestImage[0]
                }
                imagesArray.push(
                    <View key={index} style={{margin:50}}>
                        <Image
                            style={{width:180, height: 320, backgroundColor:'red'}}
                            source={{uri:'data:image/jpg;base64,'+ image}}/>
                        <Text>{info}</Text>
                    </View>
                )
             })
            this.setState({imagesArray})
        } else if (data.remindCode == 36) {
            alert('采集超时');
        }
    }

    /**
     * 检测参数配置
     */
    liveness() {
        let obj = {
            //质量校验设置
            'quality':{
                'minFaceSize' : 200,// 设置最小检测人脸阈值 默认是200
                'cropFaceSizeWidth' : 400,// 设置截取人脸图片大小 默认是 400
                'occluThreshold' : 0.5,// 设置人脸遮挡阀值 默认是 0.5
                'illumThreshold' : 40,// 设置亮度阀值 默认是 40
                'blurThreshold' : 0.7,// 设置图像模糊阀值 默认是 0.7
                'EulurAngleThrPitch' : 10,// 设置头部姿态角度 默认是 10
                'EulurAngleThrYaw' : 10,// 设置头部姿态角度 默认是 10
                'EulurAngleThrRoll' : 10,// 设置头部姿态角度 默认是 10
                'isCheckQuality' : true,// 设置是否进行人脸图片质量检测 默认是 true
                'conditionTimeout' : 10,// 设置超时时间 默认是 10
                'notFaceThreshold' : 0.6,// 设置人脸检测精度阀值 默认是0.6
                'maxCropImageNum' : 1,// 设置照片采集张数 默认是 1
            },
            'liveActionArray' :[
                0, //眨眨眼
                1, //张张嘴
                2, //向右摇头
                3, //向左摇头
                4, //抬头
                5, //低头
                6, //摇头
            ], //活体动作列表
            'order': true,//是否按顺序进行活体动作
            'sound': true, // 提示音，默认不开启
        };
        // FaceCheckHelper.openPushFaceViewController( obj );
        // 如果都不设置，需要传 {} 空对象， 建议设置 liveActionArray
        FaceCheckHelper.openPushFaceViewController( {} );
    }

    render() {
        return (
            <ScrollView style={styles.container}>
                <Text  style={styles.buttons}  onPress={() =>this.liveness()}>点击进入活体检测界面</Text>
                {this.state.imagesArray}
            </ScrollView>
        );
    }
}

const styles = StyleSheet.create({
    container: {
        flex: 1,
        // alignItems: 'center',
        // justifyContent: 'center',
        backgroundColor: '#F5FCFF',
    },
    buttons:{
        color:'black',
        fontSize:20,
        marginTop:100,
    },
});
