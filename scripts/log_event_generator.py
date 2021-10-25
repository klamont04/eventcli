from json import dumps
from faker import Faker
from random import randrange
import json
import argparse


def parse_args():
    parser = argparse.ArgumentParser(description="Logfile Event Generator")
    parser.add_argument('-f', '--filename', dest='filename', required=True,
                        help='Name of the output text file',
                        metavar='')
    parser.add_argument('-l', '--size', dest='size', type=int,
                        required=True, help='Number of events to generate in the logfile',
                        metavar='')
    return parser.parse_args()

def fake_event_generator(length, fake):
    for x in range(length):
        id = fake.word() + str(x)
        time = fake.unix_time()
        if x % 3 == 0:
            yield {"id": id, "state":"STARTED", "type":"APPLICATION_LOG", "host":"12345", "timestamp":time}, {"id":id, "state":"FINISHED", "type":"APPLICATION_LOG", "host":"12345", "timestamp":time + randrange(6)}
        else:
            yield {"id": id, "state":"STARTED", "timestamp":time}, {"id":id, "state":"FINISHED", "timestamp":time + randrange(6)}

def main():
    args = parse_args()
    filename = args.filename
    length   = args.size
    fake     = Faker() 
    Faker.seed(0)
    fpg = fake_event_generator(length, fake)
    with open('%s.txt' % filename, 'w') as output:
        i = 0
        for event in fpg:
            i=i+1
            json.dump(event[0],output)
            output.write('\n')
            json.dump(event[1],output)
            if i != length:
                output.write('\n')
    print("Done.")

if __name__ == "__main__":
    main()