const front = {
  entry: {
    front: "./front/target/scala-2.13/front-fastopt.js",
  },
  output: {
    filename: "[name].js",
    path: __dirname + "/dist",
  },
  target: "electron-renderer",
  mode: "development",
};

const worker = {
  entry: {
    worker: "./worker/target/scala-2.13/worker-fastopt.js",
  },
  output: {
    filename: "[name].js",
    path: __dirname + "/dist",
  },
  target: "webworker",
  mode: "development",
};

module.exports = [front, worker];
