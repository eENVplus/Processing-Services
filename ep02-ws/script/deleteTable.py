from model import Session
	
def deleteTable():
	session = Session()
	session.execute("DELETE FROM zones_competent_authorities;")
	session.execute("DELETE FROM zones_lau_codes;")
	session.execute("DELETE FROM zone_predecessors;")
	session.execute("DELETE FROM zone_pollutants;")
	session.execute("DELETE FROM zone_legal_acts;")
	session.execute("DELETE FROM zone_time_extension_types;")
	session.execute("DELETE FROM authorities;")
	session.execute("DELETE FROM legal_acts;")
	session.execute("DELETE FROM zones;")
	session.execute("DELETE FROM authorities;")
	session.commit()
	
if __name__ == '__main__':
	deleteTable()
