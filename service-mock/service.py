from flask import Flask, jsonify, request
from flask_cors import CORS
import os
import random

app = Flask(__name__)
CORS(app)
@app.route('/', defaults={'path': ''}, methods = ['GET'])
@app.route('/<path:path>', methods = ['GET'])
def generateId(path):
  return  jsonify(id = random.randrange(0, 101, 1))


if __name__ == '__main__':
  port = int(os.environ.get('PORT', 5000))
  app.run(host='0.0.0.0', port=port)
