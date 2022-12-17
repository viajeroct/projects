#include <fstream>
#include <iostream>
#include <string>

int main() {
	std::ios_base::sync_with_stdio(false);
	std::cin.tie(nullptr);
	std::cout.tie(nullptr);

	int num_files=20;
	
	std::ifstream IN("config");
	
	int num_of_numbers;
	IN >> num_of_numbers;
	IN.close();
	
	for (int i = 0; i < num_files; i++) {
		std::ofstream out;
		std::string name = "files_copy/file_" + std::to_string(i + 1);
		out.open(name);
		
		for (int j = 0; j < num_of_numbers; j++) {
			out << rand()%100 << " ";
		}
		
		out.close();
	}
	
	return 0;
}
