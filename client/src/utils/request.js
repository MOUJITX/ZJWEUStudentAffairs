import axios from 'axios'
import router from "../router";
import {Message, Notification} from "element-ui";

const request = axios.create({
    baseURL: '/api',  // 注意！！ 这里是全局统一加上了 '/api' 前缀，也就是说所有接口都会加上'/api'前缀在，页面里面写接口的时候就不要加 '/api'了，否则会出现2个'/api'，类似 '/api/api/user'这样的报错，切记！！！
    timeout: 60000
})


// request 拦截器
// 可以自请求发送前对请求做一些处理
// 比如统一加token，对请求参数统一加密
request.interceptors.request.use(config => {
    config.headers['Content-Type'] = 'application/json;charset=utf-8';
    let user = localStorage.getItem("UserInfo") ? JSON.parse(localStorage.getItem("UserInfo")) : {}
    config.headers['token'] = user.token;  // 设置请求头

    return config
}, error => {
    return Promise.reject(error)
});

// response 拦截器
// 可以在接口响应后统一处理结果
request.interceptors.response.use(
    response => {
        let res = response.data;
        // 如果是返回的文件
        if (response.config.responseType === 'blob') {
            return res
        }
        // 兼容服务端返回的字符串数据
        if (typeof res === 'string') {
            res = res ? JSON.parse(res) : res
        }

        if (res.code === '401'){
            router.push('/login').catch(err => { console.log(err) })
            Notification({type:'error',title: res.msg })
        }

        return res;
    },
    error => {
        console.log('err通信超时' + error) // for debug
        if (error.message.includes('timeout')){
            Notification({type:'error', title:"服务器通信超时", message:error});
        } else if (error.response.status === 413){
            Notification({type:'error', title:"数据量过大", message:error});
        } else {
            Notification({type:'error', title:"服务器通信失败", message:error});
        }
        return Promise.reject(error)
    }
)

export default request