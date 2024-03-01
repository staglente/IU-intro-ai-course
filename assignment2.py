# libraries
import random
import music21.stream
import mido
from mido import Message
from random import randint

# files for execution
INPUT = 'input1.mid'
OUTPUT = 'input1-out.mid'
# notes
NOTES = ['C', 'C#', 'D', 'D#', 'E', 'F', 'F#', 'G', 'G#', 'A', 'A#', 'B']


# dissonant pairs
DISSONANT = {
    'C': ['C#'],
    'C#': ['C', 'D'],
    'D': ['C#', 'D#'],
    'D#': ['D', 'E'],
    'E': ['D#'],
    'F': ['F#'],
    'F#': ['F', 'G'],
    'G': ['G#', 'F#'],
    'G#': ['G', 'A'],
    'A': ['G#', 'A#'],
    'A#': ['A', 'B'],
    'B': ['A#']
}

# constant offsets of chords by types
MAJOR = [0, 4, 7]
MINOR = [0, 3, 7]
INVERSE_MAJOR_1 = [0, 3, 8]
INVERSE_MAJOR_2 = [0, 5, 9]
INVERSE_MINOR_1 = [0, 4, 9]
INVERSE_MINOR_2 = [0, 5, 8]
DIMINISHED = [0, 3, 6]

# keys' table
KEYS = {
    # major ones
    'C': ['C', 'Dm', 'Em', 'F', 'G', 'Am', 'Bdim'],
    'C#': ['C#', 'D#m', 'E#m', 'F#', 'G#', 'A#m', 'Cdim'],
    'D': ['D', 'Em', 'F#m', 'G', 'A', 'Bm', 'C#dim'],
    'D#': ['D#', 'Fm', 'Gm', 'G#', 'A#', 'Cm', 'Ddim'],
    'E': ['E', 'F#m', 'G#m', 'A', 'B', 'C#m', 'D#dim'],
    'F': ['F', 'Gm', 'Am', 'A#', 'C', 'Dm', 'Edim'],
    'F#': ['F#', 'G#m', 'A#m', 'B', 'C#', 'D#m', 'E#dim'],
    'G': ['G', 'Am', 'Bm', 'C', 'D', 'Em', 'F#dim'],
    'G#': ['G#', 'A#m', 'Cm', 'C#', 'D#', 'Fm', 'Gdim'],
    'A': ['A', 'Bm', 'C#m', 'D', 'E', 'F#m', 'G#dim'],
    'A#': ['A#', 'Cm', 'Dm', 'D#', 'F', 'Gm', 'Adim'],
    'B': ['B', 'C#m', 'D#m', 'E', 'F#', 'G#m', 'A#dim'],

    # minor ones
    'Cm': ['Cm', 'Ddim', 'D#', 'Fm', 'Gm', 'G#', 'A#'],
    'C#m': ['C#m', 'D#dim', 'E', 'F#m', 'G#m', 'A', 'B'],
    'Dm': ['Dm', 'Edim', 'F', 'Gm', 'Am', 'A#', 'C'],
    'D#m': ['D#m', 'E#dim', 'F#', 'G#m', 'A#m', 'B', 'C#'],
    'Em': ['Em', 'F#dim', 'G', 'Am', 'Bm', 'C', 'D'],
    'Fm': ['Fm', 'Gdim', 'G#', 'A#m', 'Cm', 'C#', 'D#'],
    'F#m': ['F#m', 'G#dim', 'A', 'Bm', 'C#m', 'D', 'E'],
    'Gm': ['Gm', 'Adim', 'A#', 'Cm', 'Dm', 'D#', 'F'],
    'G#m': ['G#m', 'A#dim', 'B', 'C#m', 'D#m', 'E', 'F#'],
    'Am': ['Am', 'Bdim', 'C', 'Dm', 'Em', 'F', 'G'],
    'A#m': ['A#m', 'B#dim', 'C#', 'D#m', 'E#m', 'F#', 'G#'],
    'Bm': ['Bm', 'C#dim', 'D', 'Em', 'F#m', 'G', 'A']
}

# import music file and find key
score = music21.converter.parse(INPUT)
key_score = score.analyze('key')

def NoteToMidi(KeyOctave):
    octave = KeyOctave[-1]
    answer = -1
    pos = NOTES.index(key_score.name)
    answer += pos + 12 * (int(octave) + 1) + 1
    return answer

# pool of chords fill
chords = list()
key = str(key_score.tonic)
octave = max(key_score.tonic.midi // 12 - 2, 0)  # initial octave of the chords
if key_score.mode == "minor":
    key = key + 'm'
for i in KEYS[key]:
    # case of minor chord type
    if i[-1] == 'm' and not (i[-2] == 'i'):
        index = NOTES.index(i[:-1])
        chords.append([str(NOTES[(index + MINOR[0]) % len(NOTES)]) + str(octave + (index + MINOR[0]) // len(NOTES)),
                       str(NOTES[(index + MINOR[1]) % len(NOTES)]) + str(octave + (index + MINOR[1]) // len(NOTES)),
                       str(NOTES[(index + MINOR[2]) % len(NOTES)]) + str(octave + (index + MINOR[2]) // len(NOTES))])

        chords.append([str(NOTES[(index + INVERSE_MINOR_1[0]) % len(NOTES)]) + str(
            octave + (index + INVERSE_MINOR_1[0]) // len(NOTES)),
                       str(NOTES[(index + INVERSE_MINOR_1[1]) % len(NOTES)]) + str(
                           octave + (index + INVERSE_MINOR_1[1]) // len(NOTES)),
                       str(NOTES[(index + INVERSE_MINOR_1[2]) % len(NOTES)]) + str(
                           octave + (index + INVERSE_MINOR_1[2]) // len(NOTES))])

        chords.append([str(NOTES[(index + INVERSE_MINOR_2[0]) % len(NOTES)]) + str(
            octave + (index + INVERSE_MINOR_2[0]) // len(NOTES)),
                       str(NOTES[(index + INVERSE_MINOR_2[1]) % len(NOTES)]) + str(
                           octave + (index + INVERSE_MINOR_2[1]) // len(NOTES)),
                       str(NOTES[(index + INVERSE_MINOR_2[2]) % len(NOTES)]) + str(
                           octave + (index + INVERSE_MINOR_2[2]) // len(NOTES))])

    # case of diminished chord type
    elif i[-1] == 'm':
        index = NOTES.index(i[:-3])
        chords.append(
            [str(NOTES[(index + DIMINISHED[0]) % len(NOTES)]) + str(octave + (index + DIMINISHED[0]) // len(NOTES)),
             str(NOTES[(index + DIMINISHED[1]) % len(NOTES)]) + str(octave + (index + DIMINISHED[1]) // len(NOTES)),
             str(NOTES[(index + DIMINISHED[2]) % len(NOTES)]) + str(octave + (index + DIMINISHED[2]) // len(NOTES))])
    # case of major chord type
    else:
        index = NOTES.index(i)
        chords.append([str(NOTES[(index + MAJOR[0]) % len(NOTES)]) + str(octave + (index + MAJOR[0]) // len(NOTES)),
                       str(NOTES[(index + MAJOR[1]) % len(NOTES)]) + str(octave + (index + MAJOR[1]) // len(NOTES)),
                       str(NOTES[(index + MAJOR[2]) % len(NOTES)]) + str(octave + (index + MAJOR[2]) // len(NOTES))])

        chords.append([str(NOTES[(index + INVERSE_MAJOR_1[0]) % len(NOTES)]) + str(
            octave + (index + INVERSE_MAJOR_1[0]) // len(NOTES)),
                       str(NOTES[(index + INVERSE_MAJOR_1[1]) % len(NOTES)]) + str(
                           octave + (index + INVERSE_MAJOR_1[1]) // len(NOTES)),
                       str(NOTES[(index + INVERSE_MAJOR_1[2]) % len(NOTES)]) + str(
                           octave + (index + INVERSE_MAJOR_1[2]) // len(NOTES))])

        chords.append([str(NOTES[(index + INVERSE_MAJOR_2[0]) % len(NOTES)]) + str(
            octave + (index + INVERSE_MAJOR_2[0]) // len(NOTES)),
                       str(NOTES[(index + INVERSE_MAJOR_2[1]) % len(NOTES)]) + str(
                           octave + (index + INVERSE_MAJOR_2[1]) // len(NOTES)),
                       str(NOTES[(index + INVERSE_MAJOR_2[2]) % len(NOTES)]) + str(
                           octave + (index + INVERSE_MAJOR_2[2]) // len(NOTES))])

# open given melody with mido
melody = mido.MidiFile(INPUT, clip=True)
# play all tracks simultaneously
melody.type = 1

# get velocity of the given melody to assign suitable volume of the accompaniment
cnt, v = 0, 0
for x in melody.tracks[1]:
    if isinstance(x, Message) and x.type == 'note_on':
        v += x.velocity
        cnt += 1
velocity = v // cnt

# list of notes numbers in the melody for which it is needed to put a chord fill
chord_nums = []
cur, cnt = 0, 0
num = 0
for x in score.flat.notes:
    cnt += 1
    if cur == 0:
        num = cnt
    cur += x.quarterLength
    if cur == 1:
        chord_nums.append(num)
        cur = 0

# starting and ending time moments for the chords
chord_times = []
# list of notes which play simultaneously with a certain chord
chord_melody_notes = []
cur_notes = []
cnt, time_start, time_end = 0, 0, 0
for x in melody.tracks[1]:
    if (x.type == 'note_on' and (cnt + 1) in chord_nums) or x.type == 'end_of_track':
        chord_times.append([time_start, time_end])
        chord_melody_notes.append(cur_notes)
        cur_notes = []
        time_start = time_end
    if x.type == 'note_off':
        cnt += 1
        time_end += x.time
        cur_notes.append(x.note)
chord_times = chord_times[1:]
chord_melody_notes = chord_melody_notes[1:]


def fitness_func(accompaniment):  # fitness function: notes with semitone offset decrease rate with 1.5 coefficient,
    # while same NOTES increase it with 2 coefficient. Greater - better
    rate = 0
    for i in range(len(accompaniment)):
        cur_melody_notes = [NOTES[k % 12] for k in chord_melody_notes[i]]
        cur_accompaniment_chord = accompaniment[i]
        for m_note in cur_melody_notes:
            for a_c_note in cur_accompaniment_chord:
                a_c_note = a_c_note[:-1]
                if m_note == a_c_note:
                    rate += 2
                elif a_c_note in DISSONANT[m_note]:
                    rate -= 1.5
    return rate


def crossover(accompaniment_x, accompaniment_y):  # form new gen out of two other ones with random choice of the
    # corresponding genes
    new = []
    for i in range(len(accompaniment_x)):
        if random.randint(1, 10) % 2 == 0:
            new.append(accompaniment_x[i])
        else:
            new.append(accompaniment_y[i])
    return new


def mutate(accompaniment):  # change order of genes in some random way
    return random.shuffle(accompaniment)


# generate initial population with random chords
population = []
for i in range(100):
    cur_accompaniment = []
    for j in range(len(chord_melody_notes)):
        k = random.randint(0, len(chords) - 1)
        cur_accompaniment.append(chords[k])
    population.append([fitness_func(cur_accompaniment), cur_accompaniment])
population.sort()
population.reverse()

for gen in range(10):
    candidates = []
    for i in range(len(population)):
        for j in range(i + 1, len(population)):
            candidate = crossover(population[i][1], population[j][1])
            if random.randint(1, 10) % 3 == 0:
                mutate(candidate)
            candidates.append(candidate)
    ranked_candidates = []
    for i in candidates:
        ranked_candidates.append([fitness_func(i), i])
    ranked_candidates.sort()
    ranked_candidates.reverse()
    population = population[:len(population) // 2]
    ranked_candidates = ranked_candidates[:(100 - len(population))]
    for i in ranked_candidates:
        population.append(i)
    population.sort()
    population.reverse()

best = population[0][1]
track = mido.MidiTrack
for x in best:
      track.append(Message('note_on', channel=0,note = NoteToMidi(best[0]),velocity=velocity,time=0))
      track.append(Message('note_on', channel=0,note=NoteToMidi(best[1]),velocity=velocity,time=0))
      track.append(Message('note_on', channel=0,note=NoteToMidi(best[2]),velocity=velocity,time=0))
      track.append(Message('note_off', channel=0,note=NoteToMidi(best[0]),velocity=velocity,time=melody.ticks_per_beat*2))
      track.append(Message('note_off', channel=0,note=NoteToMidi(best[1]),velocity=velocity,time=0))
      track.append(Message('note_off', channel=0,note=NoteToMidi(best[2]),velocity=velocity,time=0))
melody.tracks.append(track)
melody.save(OUTPUT)
