module.exports = {
    "entry": {
        "functions-fastopt": ["/Users/schneist/Documents/GitHub/sendse/functions/target/scala-2.12/scalajs-bundler/main/functions-fastopt.js"]
    },
    "output": {
        "path": "/Users/schneist/Documents/GitHub/sendse/functions/target/scala-2.12/scalajs-bundler/main",
        "filename": "[name]-bundle.js"
    },
    "devtool": "source-map",
    "module": {
        "rules": [{
            "test": new RegExp("\\.js$"),
            "enforce": "pre",
            "use": ["source-map-loader"]
        }]
    },
    node: {
        dns: 'mock',
        fs: 'empty',
        path: true,
        url: false,
        net:true,
        ssl:true
    }
};
