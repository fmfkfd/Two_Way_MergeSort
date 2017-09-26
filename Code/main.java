import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
public class main{
	public static main obj=new main();
	int tuple_size=0;
	int total_records=0;
	int order=1;
	ArrayList<Integer> sortingorder = new ArrayList<Integer>();
	ArrayList<Integer> columnsizes = new ArrayList<Integer>();

	//printing error
	public void Print_Error(String error){
		System.out.println("################# ERROR ################\n");
		System.err.println(error);
		System.out.println("########################################\n");
		System.exit(0);
	}

	//Checking file existence
	public boolean file_exists(String F_Name){
		File f = new File(F_Name);
		if(f.exists() && !f.isDirectory()) {
			return true;
		}
		return false;
	}

	//checking isnumeric
	public boolean isNumeric(String s) {  
    	return s != null && s.matches("[-+]?\\d*\\.?\\d+");  
	}

	//complete Error Handling
	public void Errors(String args[]){
		int flag=0;
		int arg_count=args.length;
		int num_columns=0;
		String array=args[0];
		int args_test_count=0;
		int test_tuplesize=0;
		//number of arguments
		if(arg_count<5 || arg_count>7){
			//System.out.print("Arguments are:"+array+count+"\n");
			obj.Print_Error("Ther should be atleast five and maximum 7 Arguments\n");
		}
		//input file
		if(obj.file_exists(args[0])==false){
			obj.Print_Error("Input File: "+args[0]+" does not exists\n");
		}
		else{
			BufferedReader br = null;
			FileReader fr = null;
			try{
				fr = new FileReader(args[0]);
				br = new BufferedReader(fr);
				String first_line=br.readLine();
				String[] row=first_line.split("  ");
				num_columns=row.length;
				for(int i=0;i<num_columns;i++){
					obj.columnsizes.add(row[i].length());
					//System.out.println("column is "+i+" whose size is:  "+row[i].length());
				}
				//System.out.println(num_columns);
				obj.total_records=1;
				while(br.readLine()!=null)obj.total_records++;
			} 
			catch (IOException e) {
				e.printStackTrace();
			}
			finally{
				try{
					br.close();
					fr.close();
				}
				catch(IOException ex) {
					ex.printStackTrace();
				}
			}
		}

		//metadata.txt file
		if(obj.file_exists("metadata.txt")==false){
			obj.Print_Error("Matadata.txt does not exists\n");
		}
		else{
			BufferedReader br = null;
			FileReader fr = null;
			try{
				fr = new FileReader("metadata.txt");
				br = new BufferedReader(fr);
				String sCurrentLine;
				int col_pos=0;
				while ((sCurrentLine = br.readLine()) != null) {
					String[] text=sCurrentLine.split(",");
					for(int i=4;i<arg_count;i++){
						if(args[i].equals(text[0]))
							args_test_count++;
					}
					if(isNumeric(text[1])){
						obj.tuple_size+=Integer.parseInt(text[1]);
					}
					else{
						obj.Print_Error("Size of tuple should be number in metadata File");
					}
					if(Integer.parseInt(text[1])!=obj.columnsizes.get(col_pos))
						obj.Print_Error("Size of "+text[0]+"is not matching with the column of input file\n");
					if((Integer.parseInt(""+text[0].charAt(1)))+1>num_columns){
						obj.Print_Error("No of columns in metadata mismatched when compared to input file");
					}
					col_pos++;
				}
			} 
			catch (IOException e) {
				e.printStackTrace();
			} 
			finally{
				try{
					if (br != null)
						br.close();
					if (fr != null)
						fr.close();
				} 
				catch(IOException ex) {
					ex.printStackTrace();
				}
			}
		}
		//No of columns arguments
		if(args_test_count!=arg_count-4){
			Print_Error("Colums names does not exist in metadata.txt file\n");
		}
		//checking whether memory size is integer
		if(isNumeric(args[2])==false){
			Print_Error("Size of main memory shoul Be an integer\n");
			Print_Error("Sorting should be either asc or desc\n");
		}
		//checking the sorting order
		if(!(args[3].equals(new String("asc")) || args[3].equals(new String("desc")))){
			Print_Error("Sorting order should be either asc or desc\n");
		}
	}


	//comapring two rows
	public boolean comapare(String s1,String s2){
		String[] ss1=new String[obj.columnsizes.size()];
		String[] ss2=new String[obj.columnsizes.size()];
		int curr_pos=0;
		for(int i=0;i<obj.columnsizes.size();i++){
			ss1[i]=s1.substring(curr_pos,curr_pos+obj.columnsizes.get(i));
			ss2[i]=s2.substring(curr_pos,curr_pos+obj.columnsizes.get(i));
			curr_pos=curr_pos+obj.columnsizes.get(i)+2;
		}
		if(obj.order==0){
			for(int i=0;i<obj.sortingorder.size();i++){
				int k=ss1[obj.sortingorder.get(i)].compareTo(ss2[obj.sortingorder.get(i)]);
				if(k>0){
					return false;
				}
				else if(k<0){
					return true;
				}
			}
			return true;
		}
		else{
			for(int i=0;i<obj.sortingorder.size();i++){
				int k=ss1[obj.sortingorder.get(i)].compareTo(ss2[obj.sortingorder.get(i)]);
				if(k<0){
					return false;
				}
				else if(k>0){
					return true;
				}
			}
			return true;	
		}
	}

	//mergesort merging
	public void merge(String[] temp,int l,int m,int r){
		int n1=m-l+1;
		int n2=r-m;
		String[] left=new String[n1];
		String[] right=new String[n2];
		for(int i=0;i<n1;i++)
			left[i]=temp[l+i];
		for(int j=0;j<n2;j++)
			right[j]=temp[m+1+j];
		int i=0,j=0,k=l;
		while(i<n1 && j<n2){
			if(obj.comapare(left[i],right[j])){
				temp[k]=left[i];
				i++;
			}
			else{
				temp[k]=right[j];
				j++;
			}
			k++;
		}
		while(i<n1){
			temp[k]=left[i];
			i++;
			k++;
		}
		while(j<n2){
			temp[k]=right[j];
			j++;
			k++;
		}
	}

	//mergesort divding
	public void mergesort(String[] temp,int l,int r){
		if(l<r){
			int m=l+(r-l)/2;
			obj.mergesort(temp,l,m);
			obj.mergesort(temp,m+1,r);
			obj.merge(temp,l,m,r);
		}
	}

	//main function
	public static void main(String args[]){
		//errors
		obj.Errors(args);
		if(args[3].equals("asc"))
			obj.order=0;
		else
			obj.order=1;
		//sizes declaration
		int main_memory_size=Integer.parseInt(args[2]);
		//System.out.println("tuple_Size "+obj.tuple_size+" total_records "+obj.total_records);
		int records_per_file=main_memory_size/obj.tuple_size;
		if(records_per_file==0){
			obj.Print_Error("record size is greater than main memory size.Two way merge sort not sufficient\n");
		}
		int total_files=(int)Math.ceil((obj.total_records*1.0)/records_per_file);
		//System.out.println("totalfilesrequired "+total_files+" records per file "+records_per_file+" main_memory_size "+main_memory_size);
		if(total_files*obj.tuple_size>main_memory_size){
			obj.Print_Error("Two way merge sort is not sufficient\n");
		}

		//sorting order
		for(int i=4;i<args.length;i++)
		{
			obj.sortingorder.add(Integer.parseInt(""+args[i].charAt(1)));
		}
		//System.out.println("Sorting order "+obj.sortingorder);

		//########################################################
		//wrinting in to different files
		int pos_input=0;
		FileReader fr=null;
		BufferedReader br = null;
		try{
			fr= new FileReader(args[0]);
			br= new BufferedReader(fr);
		}
		catch(IOException ex){
			ex.printStackTrace();
		}
		for(int pos_file=1;pos_file<=total_files;pos_file++){
			try{
				PrintWriter writer = new PrintWriter(pos_file+".txt", "UTF-8");
				String sCurrentLine;
				int records_this_file=records_per_file;
				if(pos_file*records_per_file>obj.total_records){
					records_this_file=obj.total_records-((pos_file-1)*records_per_file);
				}
				String[] temp=new String[records_this_file];
				for(int j=pos_input,temp_pos=0;j<pos_input+records_this_file;j++,temp_pos++){
				  	if ((sCurrentLine = br.readLine()) != null) {
    					temp[temp_pos]=sCurrentLine;
    				}
    			}
				obj.mergesort(temp,0,records_this_file-1);
 				for(int i=0;i<records_this_file;i++)
 					writer.println(temp[i]);
    			writer.close();
			}
			catch (IOException e) {
				e.printStackTrace();
			}
    		pos_input=pos_input+records_per_file;
		}
		try{
			if (br != null)
				br.close();
			if (fr != null)
				fr.close();
		}
		catch(IOException ex) {
			ex.printStackTrace();
		}

		//##########################################

		//##########################################
		//merging all sorted files
		BufferedReader[] brr=new BufferedReader[total_files+1];
		FileReader[] frr=new FileReader[total_files+1];
		//PrintWriter writer=null;
		
		
		for(int i=1;i<=total_files;i++){
			try{
				frr[i]=new FileReader(i+".txt");
				brr[i]=new BufferedReader(frr[i]);
			}
			catch(IOException ex){
				ex.printStackTrace();
			}
		}
		String[] temp=new String[total_files+1];
		String sCurrentLine;
		for(int i=1;i<=total_files;i++){
			try{
				if ((sCurrentLine = brr[i].readLine()) != null) {
    				temp[i]=sCurrentLine;
    			}
    		}
    		catch(IOException ex){
				ex.printStackTrace();
			}
		}
		int completed_files=0;
		try{
			int count=0;
			PrintWriter writer = new PrintWriter(args[1], "UTF-8");
			while (completed_files!=total_files){
				//find minimum
				int min=-1;
				for(int i=1;i<=total_files;i++){
					if(temp[i].equals("$")==false){
						min=i;
						break;
					}
				}
				if(min==-1)
					break;
				for(int i=min+1;i<=total_files;i++){
					if(temp[i].equals("$")==true)
						continue;
					if(obj.comapare(temp[i],temp[min])==true){
						min=i;
					}
				}
				//System.out.println(temp[min]+" min is: "+min+"   "+count);
	 			writer.print(temp[min]+"\r\n");
				try{
					if ((sCurrentLine = brr[min].readLine()) != null) {
	    				temp[min]=sCurrentLine;
	    			}
	    			else{
	    				temp[min]="$";
	    				completed_files++;
	    			}
	    		}
	    		catch(IOException ex){
					ex.printStackTrace();
				}
				count++;
			}
    		writer.close();
		}
		catch(IOException ex){
			ex.printStackTrace();
		}

		//###########################################
		//deleting created files
		for(int i=1;i<=total_files;i++){
			try{
				File f = new File(i+".txt");
         		f.delete();
			}
			catch(Exception ex){
					ex.printStackTrace();
			}

		}
		System.out.println("-----------DONE-------------");
	}
}
