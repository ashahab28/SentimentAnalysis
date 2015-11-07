import sys
from collections import defaultdict

# Create the bigram dictionary first
word_dict = defaultdict(lambda: 0)
for line in open(sys.argv[1]):
	line = line.strip().split(',')
	content = line[0]

	# BIGRAM	
	content = content.split(' ')
	for i in range(len(content)):
		if i < len(content) - 1:
			if content[i] != '' and content[i + 1] != '':
				word = content[i] + ' ' + content[i + 1]
				word.translate(None, ",:'.")
				word_dict[word.lower()] += 1

# Create the feature file
bigram_file = open(sys.argv[2], 'w')
for key in sorted(word_dict):
	bigram_file.write(key + ',')	
bigram_file.write("label\n")

# Check each word 
counter = 0
for line in open(sys.argv[1]):
	line = line.strip().split(',')
	label = line[len(line) - 1]
	content = line[0]

	debug = ""
	if counter % 100 == 0:
		print counter

	bigram_word = []
	content = content.split(' ')
	for i in range(len(content)):
		if i < len(content) - 1:
			if content[i] != '' and content[i + 1] != '':
				temp = content[i] + ' ' + content[i + 1]
				word.translate(None, ",:'.")
				bigram_word.append(temp.lower())

	file_dict = defaultdict(lambda:0)
	for key in word_dict:
		if key in bigram_word:
			file_dict[key] += 1
		else:
			file_dict[key] = 0 
	
	for key in sorted(file_dict):
		bigram_file.write(str(file_dict[key]) + ',')
		debug += str(file_dict[key]) + ','
	bigram_file.write(label + '\n')
	counter += 1
bigram_file.close()