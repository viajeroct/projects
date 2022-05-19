#include <iostream>
#include <cstdio>
#include <vector>
#include <cstdlib>
#include <chrono>
#include <algorithm>
#include <omp.h>
#include <fstream>

using namespace std;

// TRUE => openmp works
// FALSE => without openmp
#define OPENMP_SETUP true

// Type of schedule
// Change it here, and it will change in tha whole code
#define TYPE_OF_SCHEDULE schedule(static)

// Type for one byte
typedef unsigned char Byte;

// Values from args
int numberOfThreads;
double contrastRatio;

// Possible file formats
enum FileFormats {
    PGM, PPM, WRONG
};

// Dimensions of picture
size_t cols, rows;

// Struct for .ppm picture, RGB
struct RGB {
    Byte R{}, G{}, B{};

    RGB() = default;

    RGB(Byte r, Byte g, Byte b) : R(r), G(g), B(b) {}
};

// Fixes if value is out of border
Byte fix(long double x) {
    if (x < 0) return 0;
    else if (x > 255) return 255;
    else return (Byte) x;
}

// For .ppm pictures
void convertAllRGBBytes(ifstream &input, ofstream &output) {
    // Define that reading started
    cout << "Reading image..." << "\n";
    cout.flush();

    // Read image
    size_t size = cols * rows;
    Byte *dataTemp = new Byte[size * 3];
    input.read((char *) (dataTemp), (long long) size * 3);
    vector<RGB> data(cols * rows);
    for (size_t i = 0; i < size * 3; i += 3) {
        data[i / 3] = RGB(dataTemp[i], dataTemp[i + 1], dataTemp[i + 2]);
    }

    // Start of converting
    // Ready, steady, goo!
    cout << "Image was read." << "\n";
    auto begin = chrono::steady_clock::now();
    cout << "Converting... Wait please!" << "\n";
    cout.flush();

    // Converting

    // 1. Count amount of pixels in each dimension
    vector<size_t> countR(256, 0);
    vector<size_t> countG(256, 0);
    vector<size_t> countB(256, 0);

#if OPENMP_SETUP
#pragma omp parallel default(none) shared(countR, countG, countB, data, size)
#endif
    {
        vector<size_t> countRLocal(256, 0);
        vector<size_t> countGLocal(256, 0);
        vector<size_t> countBLocal(256, 0);

#if OPENMP_SETUP
#pragma omp for TYPE_OF_SCHEDULE
#endif
        for (size_t i = 0; i < size; i++) {
            countRLocal[data[i].R]++;
            countGLocal[data[i].G]++;
            countBLocal[data[i].B]++;
        }

#if OPENMP_SETUP
#pragma omp critical
#endif
        {
            for (size_t i = 0; i < 256; i++) {
                countR[i] += countRLocal[i];
                countG[i] += countGLocal[i];
                countB[i] += countBLocal[i];
            }
        }
    }

    // 2. Find amount of ignoring pixels in each dimension
    auto amountToIgnore = (size_t) ((long double) size * contrastRatio);

    size_t lessR, lessG, lessB;
    size_t moreR, moreG, moreB;
    size_t cnt, index;

    // Counting...
    for (cnt = 0, index = 0; index < 256 && cnt <= amountToIgnore; index++) cnt += countR[index];
    lessR = index - 1;
    for (cnt = 0, index = 0; index < 256 && cnt <= amountToIgnore; index++) cnt += countG[index];
    lessG = index - 1;
    for (cnt = 0, index = 0; index < 256 && cnt <= amountToIgnore; index++) cnt += countB[index];
    lessB = index - 1;
    for (cnt = 0, index = 255; index >= 0 && cnt <= amountToIgnore; index--) cnt += countR[index];
    moreR = index + 1;
    for (cnt = 0, index = 255; index >= 0 && cnt <= amountToIgnore; index--) cnt += countG[index];
    moreG = index + 1;
    for (cnt = 0, index = 255; index >= 0 && cnt <= amountToIgnore; index--) cnt += countB[index];
    moreB = index + 1;

    // 3. Count some coefficients
    long long commonLess = (long long) min({lessR, lessG, lessB});
    long long commonMore = (long long) max({moreR, moreG, moreB});
    long long diff = commonMore - commonLess;
    long double k = 255.0 / (double) diff;

    // 4. Writing changed values
#if OPENMP_SETUP
#pragma omp parallel for default(none) shared(data, dataTemp, size, k, commonLess) TYPE_OF_SCHEDULE
#endif
    for (size_t i = 0; i < size; i++) {
        dataTemp[i * 3] = fix(k * (data[i].R - commonLess));
        dataTemp[i * 3 + 1] = fix(k * (data[i].G - commonLess));
        dataTemp[i * 3 + 2] = fix(k * (data[i].B - commonLess));
    }

    // Converting

    // End of converting, output elapsed time
    auto end = chrono::steady_clock::now();
    auto working_time = chrono::duration_cast<chrono::milliseconds>(end - begin);
    printf("Time (%i thread(s)): %ld ms\n", numberOfThreads, working_time.count());

    // Writing image back
    cout << "Writing image..." << "\n";
    output.write((char *) dataTemp, (long long) size * 3);
    cout << "Image was writen." << "\n";
}

// For .pgm pictures
void convertAllGrayBytes(ifstream &input, ofstream &output) {
    // Define that reading started
    cout << "Reading image..." << "\n";
    cout.flush();

    // Read image
    size_t size = cols * rows;
    Byte *dataTemp = new Byte[size];
    input.read((char *) (dataTemp), (long long) size);
    vector<Byte> data(cols * rows);
    for (size_t i = 0; i < size; i++) {
        data[i] = dataTemp[i];
    }

    // Start of converting
    // Ready, steady, goo!
    cout << "Image was read." << "\n";
    auto begin = chrono::steady_clock::now();
    cout << "Converting... Wait please!" << "\n";
    cout.flush();

    // Converting

    // 1. Count amount of pixels in each dimension
    vector<size_t> count(256, 0);

#if OPENMP_SETUP
#pragma omp parallel default(none) shared(count, data, size)
#endif
    {
        vector<size_t> countLocal(256, 0);

#if OPENMP_SETUP
#pragma omp for TYPE_OF_SCHEDULE
#endif
        for (size_t i = 0; i < size; i++) {
            countLocal[data[i]]++;
        }

#if OPENMP_SETUP
#pragma omp critical
#endif
        {
            for (size_t i = 0; i < 256; i++) {
                count[i] += countLocal[i];
            }
        }
    }

    // 2. Find amount of ignoring pixels in each dimension
    auto amountToIgnore = (size_t) ((long double) size * contrastRatio);

    size_t less, more, cnt, index;

    // Counting...
    for (cnt = 0, index = 0; index < 256 && cnt <= amountToIgnore; index++) cnt += count[index];
    less = index - 1;
    for (cnt = 0, index = 255; index >= 0 && cnt <= amountToIgnore; index--) cnt += count[index];
    more = index + 1;

    // 3. Count some coefficients
    auto commonLess = (long long) less;
    auto commonMore = (long long) more;
    long long diff = commonMore - commonLess;
    long double k = 255.0 / (double) diff;

    // 4. Writing changed values
#if OPENMP_SETUP
#pragma omp parallel for default(none) shared(data, dataTemp, size, k, commonLess) TYPE_OF_SCHEDULE
#endif
    for (size_t i = 0; i < size; i++) {
        dataTemp[i] = fix(k * (data[i] - commonLess));
    }

// Converting

// End of converting, output elapsed time
    auto end = chrono::steady_clock::now();
    auto working_time = chrono::duration_cast<chrono::milliseconds>(end - begin);
    printf("Time (%i thread(s)): %ld ms\n", numberOfThreads, working_time.count());

// Writing image back
    cout << "Writing image..." << "\n";
    output.write((char *) dataTemp, (long long) size);
    cout << "Image was writen." << "\n";
}

// Checks file format: ppm or pgm
FileFormats checkFileFormat(const string &fileName) {
    auto sizeOfFile = (size_t) fileName.length();
    string fileFormat = fileName.substr(sizeOfFile - 4, 4);
    if (sizeOfFile < 5) return WRONG;
    if (fileFormat == ".pgm") return PGM;
    if (fileFormat == ".ppm") return PPM;
    return WRONG;
}

// Checks that number is correct number, consists only of numbers
bool checkDigit(const string &number) {
    return all_of(number.begin(), number.end(), [](const char &it) {
        return '0' <= it && it <= '9';
    });
}

// Checks that float number is correct: 0 or 0.0 or 0.*
bool checkDoubleDigit(const string &number) {
    if (number.length() == 1 && number == "0") {
        return true;
    }
    return number[0] == '0' && number[1] == '.' && checkDigit(number.substr(2, number.size() - 2));
}

int main(int argc, char *argv[]) {
    // Checks number of argc
    if (argc != 5) {
        cout << "Expected 5 argc but found " << argc << "\n";
        cout << "Usage: main.exe <numberOfThreads> <inputFileName> <outputFileName> <contrastRatio>\n";
        return 0;
    }

    // Checks that number of threads is correct
    char *hole;
    if (!checkDigit(string(argv[1]))) {
        cout << "Expected number of threads - integer, not " << argv[1] << "\n";
        return 0;
    }

    // Number of threads
    numberOfThreads = strtol(argv[1], &hole, 10);

    // Only with openmp
#if OPENMP_SETUP && _OPENMP
    // Setup parameters for omp, default value of threads
    if (numberOfThreads != 0) {
        omp_set_num_threads(numberOfThreads);
        omp_set_dynamic(0);
    }
#endif

    // Output number of threads
    cout << "Number of threads: " << numberOfThreads << "\n";

    // Reads contrast ratio coefficient
    cout << "Input file: " << argv[2] << "\n";
    cout << "Output file: " << argv[3] << "\n";
    if (!checkDoubleDigit(argv[4])) {
        cout << "Contrast ratio expected in [0.0, 0.5) - double, but was given " << argv[4] << "\n";
        return 0;
    }
    contrastRatio = strtod(argv[4], &hole);

    // Some checks that contrast ratio coefficient is correct: in [0; 0.5)
    if (!(contrastRatio >= 0 && contrastRatio < 0.5)) {
        cout << "Contrast ratio expected in [0.0, 0.5), but was given " << contrastRatio << "\n";
        return 0;
    }
    cout << "Contrast ratio: " << contrastRatio << "\n";

    // Reading data from file
    ifstream input(argv[2], ios_base::binary);
    if (!input.is_open()) {
        cout << "File " << argv[2] << " doesn't exists!" << "\n";
        return 0;
    }

    // Get file format and check that it is .ppm or .pgm
    FileFormats first = checkFileFormat(string(argv[2]));
    FileFormats second = checkFileFormat(string(argv[3]));
    if (first == WRONG || second == WRONG || first != second) {
        cout << "Wrong file format! Or formats mismatch!" << "\n";
        return 0;
    }
    FileFormats format = first;

    // File should start with P5 or P6
    string type;
    input >> type;
    cout << "File format: " << type << "\n";
    bool headerData = type[0] == 'P' && (type[1] == '6' || type[1] == '5');
    if (!headerData) {
        cout << "Expected " << (format == PPM ? "P6" : "P5") << ".\n";
        return 0;
    }

    // Read from file dimensions of picture and maxColor=255
    input >> cols >> rows;
    cout << "Dimensions: " << cols << " " << rows << "\n";
    int maxColor;
    input >> maxColor;
    cout << "Max color value: " << maxColor << "\n";
    if (maxColor != 255) {
        cout << "Max color value expected 255 but found " <<
             maxColor << "\n";
        return 0;
    }
    input.get();

    // Output stream
    ofstream output(argv[3], ios_base::binary);
    if (!output) {
        cout << "File " << argv[3] << " doesn't exists!" << "\n";
        return 0;
    }

    // Output header of picture
    string info = (format == PPM ? "P6\n" : "P5\n")
                  + to_string(cols) + " " + to_string(rows) +
                  "\n" + to_string(maxColor) + "\n";
    output << info;

    // Convert image: ppm - RGB, pgm - Gray
    if (format == PPM) {
        convertAllRGBBytes(input, output);
    } else {
        convertAllGrayBytes(input, output);
    }

    // Close all sources
    input.close();
    output.close();
    cout << "All is done!" << "\n";

    return 0;
}
