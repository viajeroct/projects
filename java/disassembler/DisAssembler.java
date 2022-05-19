import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class DisAssembler {
    // Some constants: default value for names, rvc registers, 32i && 32m registers
    private static final String def = "DefaultNonExistingName";
    private static final String[] registers = {"s0", "s1", "a0", "a1", "a2", "a3", "a4", "a5"};
    private static final String[] bigRegisters = {"zero", "ra", "sp", "gp", "tp", "t0", "t1", "t2", "s0", "s1", "a0", "a1", "a2", "a3", "a4", "a5", "a6", "a7", "s2", "s3", "s4", "s5", "s6", "s7", "s8", "s9", "s10", "s11", "t3", "t4", "t5", "t6"};

    // Some containers: links for reverse symtab, locks for LOC05
    private static final HashMap<Long, String> links = new HashMap<>();
    private static final HashSet<Long> locsAndJumps = new HashSet<>();

    // Class for one section in header section
    private static final class HeaderSection {
        // parameters
        long NAME_OFFSET, TYPE, ADDR, OFFSET, SIZE, ENTSIZE;
        String commandName = def;

        public HeaderSection(long NAME_OFFSET, long TYPE, long ADDR, long OFFSET, long SIZE, long ENTSIZE) {
            this.NAME_OFFSET = NAME_OFFSET;
            this.TYPE = TYPE;
            this.ADDR = ADDR;
            this.OFFSET = OFFSET;
            this.SIZE = SIZE;
            this.ENTSIZE = ENTSIZE;
        }
    }

    // Class for one section in symtab section
    private static final class SymtabSection {
        long NAME_OFFSET, VALUE, SIZE;
        byte INFO, OTHER;
        int SHNDEX;
        String labelSymtabName = def;

        // Helper function: bind column
        public String getBindConverted() {
            int bind = (INFO & 0xFF) >> 4;
            if (bind == 0) return "LOCAL";
            else if (bind == 1) return "GLOBAL";
            else if (bind == 2) return "WEAK";
            else return bind >= 10 ? "RESERVED" : "";
        }

        // Helper function: type column
        public String getTypeConverted() {
            int type = (INFO & 0xF);
            if (type == 0) return "NOTYPE";
            else if (type == 1) return "OBJECT";
            else if (type == 2) return "FUNC";
            else if (type == 3) return "SECTION";
            else if (type == 4) return "FILE";
            else if (type == 5) return "COMMON";
            else if (type == 6) return "TLS";
            else return type >= 10 ? "RESERVED" : "";
        }

        // Helper function: visibility section
        public String getVisibilityConverted() {
            if (OTHER == 0) return "DEFAULT";
            else if (OTHER == 1) return "INTERNAL";
            else if (OTHER == 2) return "HIDDEN";
            else if (OTHER == 3) return "PROTECTED";
            else return "";
        }

        // Helper function: index section
        public String getIndexConverted() {
            if (SHNDEX == 0x0) return "UNDEF";
            else if (SHNDEX == 0xfff1) return "ABS";
            else if (SHNDEX == 0xfff2) return "COMMON";
            else if (SHNDEX == 0xffff) return "XINDEX";
            else return 0xff00 <= SHNDEX && SHNDEX <= 0xffff ? "RESERVED" : Integer.toString(SHNDEX);
        }

        // Output format for .symtab section
        @Override
        public String toString() {
            return String.format("0x%-15x %5d %-8s %-8s %-8s %6s %s", VALUE, SIZE, getTypeConverted(), getBindConverted(), getVisibilityConverted(), getIndexConverted(), labelSymtabName.equals("DefaultNonExistingName") ? "" : labelSymtabName);
        }

        public SymtabSection(long NAME_OFFSET, long VALUE, long SIZE, byte INFO, byte OTHER, int SHNDEX) {
            this.NAME_OFFSET = NAME_OFFSET;
            this.VALUE = VALUE;
            this.SIZE = SIZE;
            this.INFO = INFO;
            this.OTHER = OTHER;
            this.SHNDEX = SHNDEX;
        }
    }

    // Constants for pretty output, fix it
    private static final int SPACE = 21;
    private static final String SPACE_FORMAT = "%08x %" + SPACE + "s %s ";

    // Class for one command line from .text section
    private static final class TextSection {
        long LOCATION; // address
        String LABEL_COMMAND, NAME; // label command: LOC or name from .symtab
        String[] ARGUMENTS; // arguments for command: LOC or registers or numbers

        public TextSection(long LOCATION, String LABEL_COMMAND, String NAME, String[] ARGUMENTS) {
            this.LOCATION = LOCATION;
            this.LABEL_COMMAND = LABEL_COMMAND;
            this.NAME = NAME;
            this.ARGUMENTS = ARGUMENTS;
        }

        // Output format for one command line:
        // To fix spaces modify SPACE variable, initially 21
        @Override
        public String toString() {
            return String.format(SPACE_FORMAT, LOCATION, LABEL_COMMAND.equals(def) || LABEL_COMMAND.isEmpty() ? "" : (LABEL_COMMAND + ":"), NAME) + String.join(", ", ARGUMENTS);
        }
    }

    // Class for command's immediate
    private static final class Immediate {
        // Function that gets imm
        private static int getImmediate(int x, int immType) {
            if (immType == 1)
                return (getBitsFromNumber(x, 8, 8) << 10) + (getBitsFromNumber(x, 9, 10) << 8) + (getBitsFromNumber(x, 6, 6) << 7) + (getBitsFromNumber(x, 7, 7) << 6) + (getBitsFromNumber(x, 2, 2) << 5) + (getBitsFromNumber(x, 11, 11) << 4) + (getBitsFromNumber(x, 3, 5) << 1) - getBitsFromNumber(x, 12, 12) * (1 << 11);
            else if (immType == 2)
                return (getBitsFromNumber(x, 5, 6) << 6) + (getBitsFromNumber(x, 2, 2) << 5) + (getBitsFromNumber(x, 10, 11) << 3) + (getBitsFromNumber(x, 3, 4) << 1) - getBitsFromNumber(x, 12, 12) * (1 << 8);
            else if (immType == 3) return getBitsFromNumber(x, 2, 6) - getBitsFromNumber(x, 12, 12) * (1 << 5);
            else if (immType == 4)
                return (getBitsFromNumber(x, 7, 7) << 11) + (getBitsFromNumber(x, 25, 30) << 5) + (getBitsFromNumber(x, 8, 11) << 1) - getBitsFromNumber(x, 31, 31) * (1 << 12);
            else if (immType == 5) return getBitsFromNumber(x, 20, 30) - getBitsFromNumber(x, 31, 31) * (1 << 11);
            else if (immType == 6)
                return (getBitsFromNumber(x, 25, 30) << 5) + getBitsFromNumber(x, 7, 11) - getBitsFromNumber(x, 31, 31) * (1 << 11);
            else if (immType == 7)
                return (getBitsFromNumber(x, 21, 30) << 1) + (getBitsFromNumber(x, 20, 20) << 11) + (getBitsFromNumber(x, 12, 19) << 12) - getBitsFromNumber(x, 31, 31) * (1 << 20);
            else if (immType == 8) return getBitsFromNumber(x, 12, 31) << 12;
            else throw new IllegalArgumentException("Check immediate type");
        }
    }
    // Description (types - immediate):
    // (1, IMM11); (2, IMM8); (3, NZIMM6); (4, BIMM); (5, IIMM); (6, SIMM); (7, JIMM); (8, UIMM);

    // Returns label if it contains in symtab otherwise LOC_%05x
    private static String getLabels(long location) {
        locsAndJumps.add(location);
        return links.getOrDefault(location, String.format("LOC_%05x", location));
    }

    // Returns bits from got number from 'from' to 'to' (LE)
    private static int getBitsFromNumber(int num, int from, int to) {
        String cur = new StringBuilder(Integer.toBinaryString(num)).reverse().toString();
        StringBuilder res = new StringBuilder();
        for (int i = to; i >= from; i--) {
            if (i >= cur.length()) res.append('0');
            else res.append(cur.charAt(i));
        }
        return Integer.parseInt(res.toString(), 2);
    }

    // Function for reading bytes from file (data) from 'offset' size elements, size exclusive, Long type
    private static long getDataBytesFromFile(byte[] data, int offset, int size) {
        long answer = 0;
        for (int i = offset; i < offset + size; i++) {
            answer += (data[i] & 255L) << (8 * (i - offset));
        }
        return answer;
    }

    // Call of higher function and casting to int
    // 4 - inlined, only this size using
    private static int getDataBytesCastToInt(byte[] data, int offset) {
        return (int) getDataBytesFromFile(data, offset, 4);
    }

    // Function like functions higher but returns string, not number
    // '\0' - all string are null terminated
    private static String getStringData(byte[] data, int offset, int whereStarts) {
        int pos = whereStarts + offset;
        StringBuilder res = new StringBuilder();
        while (pos < data.length && data[pos] != '\0') {
            res.append((char) data[pos]);
            pos++;
        }
        return res.toString();
    }

    public static void main(String[] args) {
        // Fields in header section, header data, wikipedia
        byte CLASS, DATA;
        long MACHINE, SHOFF, SHENTSIZE, SHNUM, SHSTRNDX;

        // Check program arguments
        if (args.length != 2) {
            System.err.println("Smth wrong with arguments, need input and output file: " + Arrays.toString(args));
            return;
        }

        // Input and output file names from args
        String inputFileName = args[0];
        String outputFileName = args[1];

        // Input, parsing, output
        try {
            // Read all bytes from file
            byte[] data = Files.readAllBytes(Path.of(inputFileName));

            // Check that file is ELF, all ELF files starts from ?ELF
            if (data.length < 4 || !(data[0] == '\u007F' && data[1] == 'E' && data[2] == 'L' && data[3] == 'F')) {
                System.err.println("This file is not ELF :( File should be ELF!");
                return;
            }

            // Parse ELF file header
            CLASS = data[0x04];
            DATA = data[0x05];
            MACHINE = getDataBytesFromFile(data, 0x12, 2);
            SHOFF = getDataBytesFromFile(data, 0x20, 4);
            SHENTSIZE = getDataBytesFromFile(data, 0x2E, 2);
            SHNUM = getDataBytesFromFile(data, 0x30, 2);
            SHSTRNDX = getDataBytesFromFile(data, 0x32, 2);

            // Some checks that file is ELF file
            if (DATA != 0x1 || MACHINE != 0xF3 || CLASS != 0x1 || SHENTSIZE != 40) {
                System.err.println("Something wrong with your ELF file :(( Check it!");
                return;
            }

            // Array for header sections
            ArrayList<HeaderSection> headerSections = new ArrayList<>();

            // Reading of header sections
            for (int i = 0; i < SHNUM; i++) {
                int pos = (int) (SHOFF + SHENTSIZE * i);
                headerSections.add(new HeaderSection(getDataBytesFromFile(data, pos, 4), getDataBytesFromFile(data, pos + 0x04, 4), getDataBytesFromFile(data, pos + 0x0C, 4), getDataBytesFromFile(data, pos + 0x10, 4), getDataBytesFromFile(data, pos + 0x14, 4), getDataBytesFromFile(data, pos + 0x24, 4)));
            }

            // Read all names for sections, header names table offset - SHSTRNDX
            int whereStartNamesGlobally = (int) headerSections.get((int) SHSTRNDX).OFFSET;
            for (HeaderSection current : headerSections)
                if (current.TYPE != '\0')
                    current.commandName = getStringData(data, (int) current.NAME_OFFSET, whereStartNamesGlobally);

            // Finding data headers for .text and .symtab sections
            HeaderSection textHeaderData = null;
            HeaderSection symtabHeaderData = null;
            int nameTableOffsetStrTab = -1;
            for (HeaderSection section : headerSections) {
                switch (section.commandName) {
                    // .text section
                    case ".text" -> textHeaderData = section;
                    // .symtab section
                    case ".symtab" -> symtabHeaderData = section;
                    // .strtab section, names
                    case ".strtab" -> nameTableOffsetStrTab = (int) section.OFFSET;
                }
            }

            // Another some checks that ELF file is correct ELF file
            // <=> not found some data about headers
            if (nameTableOffsetStrTab == -1 || textHeaderData == null || symtabHeaderData == null || symtabHeaderData.ENTSIZE != 16) {
                System.err.println("Something wrong with your ELF file! Check it please!");
                return;
            }

            // Finally, parsing of .symtab section
            // Array for .symtab sections
            ArrayList<SymtabSection> symtab = new ArrayList<>();
            int linesInSymtab = (int) (symtabHeaderData.SIZE / symtabHeaderData.ENTSIZE);
            for (int i = 0; i < linesInSymtab; i++) {
                int pos = (int) symtabHeaderData.OFFSET + 16 * i;
                SymtabSection currentSection = new SymtabSection(getDataBytesFromFile(data, pos, 4), getDataBytesFromFile(data, pos + 0x4, 4), getDataBytesFromFile(data, pos + 0x8, 4), data[pos + 0xC], data[pos + 0xD], (int) getDataBytesFromFile(data, pos + 0xE, 2));
                currentSection.labelSymtabName = currentSection.NAME_OFFSET > 0 ? getStringData(data, (int) currentSection.NAME_OFFSET, nameTableOffsetStrTab) : currentSection.labelSymtabName;
                symtab.add(currentSection);
            }

            // Begin of parsing .text section

            // Create HashMap for links - for LOCS and commands args: addr - label
            for (SymtabSection section : symtab)
                if (!section.labelSymtabName.equals(def) && section.VALUE != 0 && (section.INFO & 0xF) == 2)
                    links.put(section.VALUE, section.labelSymtabName);

            // Parse .text section commands, compressed, uncompressed, rvc, rv32i, rv32m - need to parse

            // Array for all commands
            ArrayList<TextSection> text = new ArrayList<>();

            // Loop for all commands
            int pos = (int) textHeaderData.OFFSET;
            while (pos < textHeaderData.OFFSET + textHeaderData.SIZE) {

                // Meta info - address, command data
                long locationAddr = pos - textHeaderData.OFFSET + textHeaderData.ADDR;
                int commandDataBytes = getDataBytesCastToInt(data, pos);

                // Local variables, in the end I create object
                String currentName = "";
                String[] currentArguments = new String[0];

                // Type of command - compressed or not
                if ((data[pos] & 0x3) == 3) {
                    int groupVar = getBitsFromNumber(commandDataBytes, 12, 14);
                    int innerVar = getBitsFromNumber(commandDataBytes, 2, 6);

                    if (innerVar == 0x18) {
                        boolean correct = true;
                        if (groupVar == 0) currentName = "beq";
                        else if (groupVar == 1) currentName = "bne";
                        else if (groupVar == 4) currentName = "blt";
                        else if (groupVar == 5) currentName = "bge";
                        else if (groupVar == 6) currentName = "bltu";
                        else if (groupVar == 7) currentName = "bgeu";
                        else correct = false;
                        if (correct)
                            currentArguments = new String[]{bigRegisters[getBitsFromNumber(commandDataBytes, 15, 19)], bigRegisters[getBitsFromNumber(commandDataBytes, 20, 24)], getLabels(locationAddr + Immediate.getImmediate(commandDataBytes, 4))};
                    } else if (innerVar == 0x19 && groupVar == 0) {
                        currentName = "jalr";
                        currentArguments = new String[]{bigRegisters[getBitsFromNumber(commandDataBytes, 7, 11)], bigRegisters[getBitsFromNumber(commandDataBytes, 15, 19)], Integer.toString(Immediate.getImmediate(commandDataBytes, 5))};
                    } else if (innerVar == 0x1B) {
                        currentName = "jal";
                        currentArguments = new String[]{bigRegisters[getBitsFromNumber(commandDataBytes, 7, 11)], getLabels(locationAddr + Immediate.getImmediate(commandDataBytes, 7))};
                    } else if (innerVar == 0x0D) {
                        currentName = "lui";
                        currentArguments = new String[]{bigRegisters[getBitsFromNumber(commandDataBytes, 7, 11)], Integer.toString(Immediate.getImmediate(commandDataBytes, 8))};
                    } else if (innerVar == 0x05) {
                        currentName = "auipc";
                        currentArguments = new String[]{bigRegisters[getBitsFromNumber(commandDataBytes, 7, 11)], Integer.toString(Immediate.getImmediate(commandDataBytes, 8))};
                    } else if (innerVar == 0x04) {
                        boolean correct = true;
                        if (groupVar == 0) currentName = "addi";
                        else if (groupVar == 2) currentName = "slti";
                        else if (groupVar == 3) currentName = "sltiu";
                        else if (groupVar == 4) currentName = "xori";
                        else if (groupVar == 6) currentName = "ori";
                        else if (groupVar == 7) currentName = "andi";
                        else correct = false;
                        if (correct)
                            currentArguments = new String[]{bigRegisters[getBitsFromNumber(commandDataBytes, 7, 11)], bigRegisters[getBitsFromNumber(commandDataBytes, 15, 19)], Integer.toString(Immediate.getImmediate(commandDataBytes, 5))};
                        if (groupVar == 1 || groupVar == 5) {
                            if (groupVar == 1) currentName = "slli";
                            else if (getBitsFromNumber(commandDataBytes, 25, 31) == 0) currentName = "srli";
                            else currentName = "srai";
                            currentArguments = new String[]{bigRegisters[getBitsFromNumber(commandDataBytes, 7, 11)], bigRegisters[getBitsFromNumber(commandDataBytes, 15, 19)], Integer.toString(getBitsFromNumber(commandDataBytes, 20, 24))};
                        }
                    } else if (innerVar == 0x0C) {
                        boolean correct = true;
                        int curCode = getBitsFromNumber(commandDataBytes, 25, 31);
                        if (groupVar == 0) {
                            if (curCode == 0) currentName = "add";
                            else if (curCode == 1) currentName = "mul";
                            else if (curCode == 32) currentName = "sub";
                            else correct = false;
                        } else if (groupVar == 1) {
                            if (curCode == 0) currentName = "sll";
                            else if (curCode == 1) currentName = "mulh";
                            else correct = false;
                        } else if (groupVar == 2) {
                            if (curCode == 0) currentName = "slt";
                            else if (curCode == 1) currentName = "mulhsu";
                            else correct = false;
                        } else if (groupVar == 3) {
                            if (curCode == 0) currentName = "sltu";
                            else if (curCode == 1) currentName = "mulhu";
                            else correct = false;
                        } else if (groupVar == 4) {
                            if (curCode == 0) currentName = "xor";
                            else if (curCode == 1) currentName = "div";
                            else correct = false;
                        } else if (groupVar == 5) {
                            if (curCode == 0) currentName = "srl";
                            else if (curCode == 1) currentName = "divu";
                            else if (curCode == 32) currentName = "sra";
                            else correct = false;
                        } else if (groupVar == 6) {
                            if (curCode == 0) currentName = "or";
                            else if (curCode == 1) currentName = "rem";
                            else correct = false;
                        } else if (groupVar == 7) {
                            if (curCode == 0) currentName = "and";
                            else if (curCode == 1) currentName = "remu";
                            else correct = false;
                        } else correct = false;
                        if (correct)
                            currentArguments = new String[]{bigRegisters[getBitsFromNumber(commandDataBytes, 7, 11)], bigRegisters[getBitsFromNumber(commandDataBytes, 15, 19)], bigRegisters[getBitsFromNumber(commandDataBytes, 20, 24)]};
                    } else if (innerVar == 0x00) {
                        boolean correct = true;
                        if (groupVar == 0) currentName = "lb";
                        else if (groupVar == 1) currentName = "lh";
                        else if (groupVar == 2) currentName = "lw";
                        else if (groupVar == 4) currentName = "lbu";
                        else if (groupVar == 5) currentName = "lhu";
                        else correct = false;
                        if (correct)
                            currentArguments = new String[]{bigRegisters[getBitsFromNumber(commandDataBytes, 7, 11)], String.format("%d(%s)", Immediate.getImmediate(commandDataBytes, 5), bigRegisters[getBitsFromNumber(commandDataBytes, 15, 19)])};
                    } else if (innerVar == 0x08) {
                        boolean correct = true;
                        if (groupVar == 0) currentName = "sb";
                        else if (groupVar == 1) currentName = "sh";
                        else if (groupVar == 2) currentName = "sw";
                        else correct = false;
                        if (correct)
                            currentArguments = new String[]{bigRegisters[getBitsFromNumber(commandDataBytes, 20, 24)], String.format("%d(%s)", Immediate.getImmediate(commandDataBytes, 6), bigRegisters[getBitsFromNumber(commandDataBytes, 15, 19)])};
                    } else if (innerVar == 0x1C) {
                        if (groupVar == 0) {
                            // Need to parse system commands
                            // Fence not needed
                            int curCode = getBitsFromNumber(commandDataBytes, 20, 31);
                            if (curCode == 0) currentName = "ecall";
                            else if (curCode == 1) currentName = "ebreak";
                        } else {
                            if (groupVar == 1) currentName = "csrrw";
                            else if (groupVar == 2) currentName = "csrrs";
                            else if (groupVar == 3) currentName = "csrrc";
                            else if (groupVar == 5) currentName = "csrrwi";
                            else if (groupVar == 6) currentName = "csrrwi";
                            else if (groupVar == 7) currentName = "csrrci";
                            currentArguments = new String[]{bigRegisters[getBitsFromNumber(commandDataBytes, 7, 11)], Integer.toString(getBitsFromNumber(commandDataBytes, 20, 31)), bigRegisters[getBitsFromNumber(commandDataBytes, 15, 19)]};
                        }
                    }

                    // Go to the next command, length of uncompressed command is 4 bytes
                    pos += 4;
                } else {
                    // Parsing compressed rvc commands
                    int nameCommand = getBitsFromNumber(commandDataBytes, 13, 15);
                    int nameGroup = getBitsFromNumber(commandDataBytes, 0, 1);

                    if (nameGroup == 0) {
                        if (nameCommand == 0) {
                            currentName = "c.addi4spn";
                            int curImm = (getBitsFromNumber(commandDataBytes, 7, 10) << 6) + (getBitsFromNumber(commandDataBytes, 11, 12) << 4) + (getBitsFromNumber(commandDataBytes, 5, 5) << 3) + (getBitsFromNumber(commandDataBytes, 6, 6) << 2);
                            currentArguments = new String[]{registers[getBitsFromNumber(commandDataBytes, 2, 4)], "sp", Integer.toString(curImm)};
                        } else if (nameCommand == 2 || nameCommand == 6) {
                            int curImm = (getBitsFromNumber(commandDataBytes, 5, 5) << 6) + (getBitsFromNumber(commandDataBytes, 10, 12) << 3) + (getBitsFromNumber(commandDataBytes, 6, 6) << 2);
                            currentArguments = new String[]{registers[getBitsFromNumber(commandDataBytes, 2, 4)], String.format("%d(%s)", curImm, registers[getBitsFromNumber(commandDataBytes, 7, 9)])};
                        }
                        if (nameCommand == 2) currentName = "c.lw";
                        else if (nameCommand == 6) currentName = "c.sw";
                    } else if (nameGroup == 1) {
                        if (nameCommand == 0) {
                            if (getBitsFromNumber(commandDataBytes, 2, 15) == 0) currentName = "c.nop";
                            else {
                                currentName = "c.addi";
                                currentArguments = new String[]{bigRegisters[getBitsFromNumber(commandDataBytes, 7, 11)], Integer.toString(Immediate.getImmediate(commandDataBytes, 3))};
                            }
                        } else if (nameCommand == 1) {
                            currentName = "c.jal";
                            currentArguments = new String[]{getLabels(locationAddr + Immediate.getImmediate(commandDataBytes, 1))};
                        } else if (nameCommand == 2) {
                            currentName = "c.li";
                            currentArguments = new String[]{bigRegisters[getBitsFromNumber(commandDataBytes, 7, 11)], Integer.toString(Immediate.getImmediate(commandDataBytes, 3))};
                        } else if (nameCommand == 3) {
                            int rdPart = getBitsFromNumber(commandDataBytes, 7, 11);
                            if (rdPart == 2) {
                                currentName = "c.addi16sp";
                                currentArguments = new String[]{"sp", "sp", Integer.toString((getBitsFromNumber(commandDataBytes, 3, 4) << 7) + (getBitsFromNumber(commandDataBytes, 5, 5) << 6) + (getBitsFromNumber(commandDataBytes, 2, 2) << 5) + (getBitsFromNumber(commandDataBytes, 6, 6) << 4) - getBitsFromNumber(commandDataBytes, 12, 12) * (1 << 9))};
                            } else {
                                currentName = "c.lui";
                                currentArguments = new String[]{bigRegisters[rdPart], Integer.toString(Immediate.getImmediate(commandDataBytes, 3) << 12)};
                            }
                        } else if (nameCommand == 4) {
                            int curCode = getBitsFromNumber(commandDataBytes, 10, 11);
                            if (curCode == 0) {
                                currentName = "c.srli";
                                currentArguments = new String[]{registers[getBitsFromNumber(commandDataBytes, 7, 9)], Integer.toString(Immediate.getImmediate(commandDataBytes, 3))};
                            } else if (curCode == 1) {
                                currentName = "c.srai";
                                currentArguments = new String[]{registers[getBitsFromNumber(commandDataBytes, 7, 9)], Integer.toString(Immediate.getImmediate(commandDataBytes, 3))};
                            } else if (curCode == 2) {
                                currentName = "c.andi";
                                currentArguments = new String[]{registers[getBitsFromNumber(commandDataBytes, 7, 9)], Integer.toString(Immediate.getImmediate(commandDataBytes, 3))};
                            } else if (curCode == 3) {
                                int name = getBitsFromNumber(commandDataBytes, 5, 6);
                                if (name == 0) currentName = "c.sub";
                                else if (name == 1) currentName = "c.xor";
                                else if (name == 2) currentName = "c.or";
                                else if (name == 3) currentName = "c.and";
                                else currentName = "";
                                currentArguments = new String[]{registers[getBitsFromNumber(commandDataBytes, 7, 9)], registers[getBitsFromNumber(commandDataBytes, 2, 4)]};
                            }
                        } else if (nameCommand == 5) {
                            currentName = "c.j";
                            currentArguments = new String[]{getLabels(locationAddr + Immediate.getImmediate(commandDataBytes, 1))};
                        } else if (nameCommand == 6) {
                            currentName = "c.beqz";
                            currentArguments = new String[]{registers[getBitsFromNumber(commandDataBytes, 7, 9)], getLabels(locationAddr + Immediate.getImmediate(commandDataBytes, 2))};
                        } else if (nameCommand == 7) {
                            currentName = "c.bnez";
                            currentArguments = new String[]{registers[getBitsFromNumber(commandDataBytes, 7, 9)], getLabels(locationAddr + Immediate.getImmediate(commandDataBytes, 2))};
                        }
                    } else if (nameGroup == 2) {
                        if (nameCommand == 0) {
                            currentName = "c.slli";
                            currentArguments = new String[]{bigRegisters[getBitsFromNumber(commandDataBytes, 7, 11)], Integer.toString(Immediate.getImmediate(commandDataBytes, 3))};
                        } else if (nameCommand == 2) {
                            currentName = "c.lwsp";
                            int imm = (getBitsFromNumber(commandDataBytes, 2, 3) << 6) + (getBitsFromNumber(commandDataBytes, 12, 12) << 5) + (getBitsFromNumber(commandDataBytes, 4, 6) << 2);
                            currentArguments = new String[]{bigRegisters[getBitsFromNumber(commandDataBytes, 7, 11)], String.format("%d(sp)", imm)};
                        } else if (nameCommand == 4) {
                            int rsPartFirst = getBitsFromNumber(commandDataBytes, 7, 11);
                            int rsPartSecond = getBitsFromNumber(commandDataBytes, 2, 6);
                            if (getBitsFromNumber(commandDataBytes, 12, 12) == 0) {
                                if (rsPartSecond == 0) {
                                    currentName = "c.jr";
                                    currentArguments = new String[]{bigRegisters[rsPartFirst]};
                                } else {
                                    currentName = "c.mv";
                                    currentArguments = new String[]{bigRegisters[rsPartFirst], bigRegisters[rsPartSecond]};
                                }
                            } else {
                                if (rsPartFirst == 0 && rsPartSecond == 0) currentName = "c.ebreak";
                                else if (rsPartSecond == 0) {
                                    currentName = "c.jalr";
                                    currentArguments = new String[]{bigRegisters[rsPartFirst]};
                                } else {
                                    currentName = "c.add";
                                    currentArguments = new String[]{bigRegisters[rsPartFirst], bigRegisters[rsPartSecond]};
                                }
                            }
                        } else if (nameCommand == 6) {
                            currentName = "c.swsp";
                            int imm = (getBitsFromNumber(commandDataBytes, 7, 8) << 6) + (getBitsFromNumber(commandDataBytes, 9, 12) << 2);
                            currentArguments = new String[]{bigRegisters[getBitsFromNumber(commandDataBytes, 2, 6)], String.format("%d(sp)", imm)};
                        }
                    }

                    // Go to the next command, size of one compressed command - 2 bytes
                    pos += 2;
                }

                // Add this command to array
                text.add(new TextSection(locationAddr, links.getOrDefault(locationAddr, ""), currentName, currentArguments));
            }

            // Adding LOCS if wasn't found in .symtab section into the beginning of command
            for (TextSection current : text)
                if (current.LABEL_COMMAND.isEmpty() && locsAndJumps.contains(current.LOCATION))
                    current.LABEL_COMMAND = String.format("LOC_%05x", current.LOCATION);

            // Output source
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFileName)));

            // Write .text section
            out.write(".text");
            out.newLine();

            // Form output for .text sections, see toString @Override
            for (TextSection current : text) {
                out.write(current.toString());
                out.newLine();
            }
            out.newLine();

            // Write .symtab section
            out.write(".symtab");
            out.newLine();

            // Form output for .text sections, see toString @Override
            // Concatenating all sections, see task
            out.write(String.format("%s %-15s %7s %-8s %-8s %-8s %6s %s", "Symbol", "Value", "Size", "Type", "Bind", "Vis", "Index", "Name"));
            out.newLine();
            for (int i = 0; i < symtab.size(); i++) {
                out.write(String.format("[%4d] %s", i, symtab.get(i)));
                out.newLine();
            }

            // Closing of resources - output
            out.close();
        } catch (IOException e) {
            // Exception with IO
            System.err.println("Something went wrong with input / output files: " + e.getMessage());
        } catch (Exception e) {
            // Others errors
            System.err.println("Something went wrong (Exception) : " + e.getMessage());
        }
    }
}
