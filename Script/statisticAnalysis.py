import sys
from collections import defaultdict

word_dict = defaultdict(lambda: 0)
for line in open('jokowi_sort_uniq.csv'):
	line = line.strip().split(',')
	content = line[0]
	# UNIGRAM
	#for word in content.split(' '):
	#	word.translate(None, ",:'\.")
	#	
	#	word_dict[word.lower()] += 1

	# BIGRAM	
	content = content.split(' ')
	for i in range(len(content)):
		if i < len(content) - 5:
			if content[i] != '' and content[i + 1] != '' and content[i + 2]!= '' and content[i + 3]!= '' and content[i + 4] != '':
				word = content[i] + ' ' + content[i + 1] + ' ' + content[i + 2] + ' ' + content[i + 3] + ' ' + content[i + 4]
				word.translate(None, ",:'.")
				word_dict[word.lower()] += 1
	
i = 0
for w in sorted(word_dict, key=word_dict.get, reverse=True):
	print w+":"+str(word_dict[w])
	i += 1
	if(i > 10): break

