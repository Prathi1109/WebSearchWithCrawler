
import json
import nltk
from nltk.corpus import stopwords
from nltk.stem import WordNetLemmatizer
from glove import Corpus,Glove
import string
#import numpy
#from sklearn.manifold import TSNE
#import matplotlib.pyplot as plt
import csv
import tensorflow as tf
from tensorflow.contrib.tensorboard.plugins import projector

stop_words=set(stopwords.words("english")) 
wordnetlemmatizer=WordNetLemmatizer()
#print(stop_words)
jsonFile="/Volumes/Untitled/WithDescription/CS/visualize.json"

with open(jsonFile,"r",encoding="utf-8") as fin:
    jsonLines=list( fin.readlines())
    print(jsonFile + "reading successful")
    fulltext= []
    description=[]
    def preprocess(content):
        tokens= nltk.word_tokenize(content)
        #print(tokens)
        temp_line=[]
        for word in tokens:
            if word not in stop_words:
               temp_line.append(word)
        #print(temp_line)
        lemmatize=[]
        for word in temp_line:
            lemmatize.append(wordnetlemmatizer.lemmatize(word))
            
        #print(lemmatize)  
        noPunctutations=[]
        for item in lemmatize:
            if item not in string.punctuation:
               noPunctutations.append(item)
        
        return noPunctutations;
    
    for l in jsonLines:
        temp=json.loads(l)
        #tokenize, lemmatize, stopword,punctutions removal
        contentList = preprocess(temp["content"])
        
        descriptionList= preprocess(temp["description"])
        
        fulltext.append(contentList)
        description.append(descriptionList)
        #fulltext+=temp["content"]
    print(fulltext)
    
   # print(desc_glove.word_vectors[desc_glove.dictionary['eat']])
  
   # print(glove.dictionary['I'])
    """
    full_text=[]
    for sublist in fulltext:
        for item in sublist:
            full_text.append(item)
    desc_text=[]
    for sublist in description:
        for item in sublist:
            desc_text.append(item)
    """        
   
    print(len(fulltext))
    print(len(fulltext[0]))
    print(len(fulltext[1]))
  
    
    
    corpus=Corpus()
    desc=Corpus()
    corpus.fit(fulltext,window=10) # length of the (symmetric)context window used for cooccurrence
    desc.fit(description,window=10)
    desc_glove= Glove(no_components = 100 ,learning_rate=0.05)
    desc_glove.fit(desc.matrix,epochs=30,no_threads=4,verbose=True)
    desc_glove.add_dictionary(desc.dictionary)
    desc_glove.save('/Volumes/Untitled/WithDescription/CS/desc_glove.tsv')
    glove= Glove(no_components=390,learning_rate=0.05)
    glove.fit(corpus.matrix,epochs=30,no_threads=4,verbose=True)
    glove.add_dictionary(corpus.dictionary)
    #tsne = TSNE(n_components=2, verbose=1,perplexity=2,method='exact')
    #tsne_results = tsne.fit_transform(desc_glove.word_vectors)
    print(corpus.dictionary)
    content_vector=glove.word_vectors  #vector with word embeddings

    with open("/Volumes/Untitled/WithDescription/CS/content.tsv","w+") as my_csv:
       
         csvWriter = csv.writer(my_csv,delimiter=' ')
         csvWriter.writerows(content_vector)
         
    with open('/Volumes/Untitled/WithDescription/CS/content_label.tsv', 'w', newline='') as f_output:
         tsv_output = csv.writer(f_output, delimiter='\n')
         tsv_output.writerow(corpus.dictionary)
   
   
#Tensor board implementation
         
sess = tf.InteractiveSession()  
with tf.device("/cpu:0"):
    embedding = tf.Variable(content_vector, trainable=False, name='embedding')
tf.global_variables_initializer().run()
path = 'tensorboard'
saver = tf.train.Saver()
writer = tf.summary.FileWriter(path, sess.graph)   

config = projector.ProjectorConfig()
embed = config.embeddings.add()
embed.tensor_name = 'embedding'
embed.metadata_path = '/Volumes/Untitled/WithDescription/CS/content_label.tsv' 
projector.visualize_embeddings(writer, config)

saver.save(sess, path+'/glove.ckpt')



   
"""
#TSNE MATPLOT Implementation

    with open("/Volumes/Untitled/WithDescription/CS/prat.tsv","w+") as my_csv:
       
         csvWriter = csv.writer(my_csv,delimiter=' ')
         csvWriter.writerows(prat)
         
    with open('/Volumes/Untitled/WithDescription/CS/prat_label.tsv', 'w', newline='') as f_output:
         tsv_output = csv.writer(f_output, delimiter='\n')
         tsv_output.writerow(desc_text)
         
         
         
    plt.scatter(tsne_results[:,0],tsne_results[:,1],cmap=plt.get_cmap('Greens'))
for label,x,y in zip(desc_text,tsne_results[:,0],tsne_results[:,1]):
    plt.annotate(
        label,
        xy=(x,y),
        xytext=(-14, 14),
        textcoords='offset points',
        #bbox=dict(boxstyle='round,pad=0.5', fc='yellow', alpha=0.5),
        bbox=dict(boxstyle='round,pad=0.5', fc='yellow', alpha=0.5),
        arrowprops=dict(arrowstyle = '->', connectionstyle='arc3,rad=0')
    )
plt.xlabel('TSNE Component 1 ')
plt.ylabel('TSNE Component 2')
plt.title('TSNE representation for Word Embedding')
"""
