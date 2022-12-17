#include <fstream>
#include <iostream>
#include <string>

int main() {
	std::string file_name;
	std::cin >> file_name;

	std::ifstream in("files/" + file_name);
	std::ifstream IN("config");
	
	int num_of_numbers;
	IN >> num_of_numbers;
	IN.close();
	
	std::ofstream out("files/" + file_name, std::ios::app);
	for (int i = 0; i < num_of_numbers; i++) {
		int x;
		in >> x;
		out << 2 * x << " ";
	}
	
	in.close();
	out.close();
	
	std::cout << "done" << std::endl;

	return 0;
}
