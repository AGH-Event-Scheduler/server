package pl.edu.agh.server.domain.exception

class OrganizationNotFoundException(organizationId: Long) :
    RuntimeException("Organization with ID $organizationId not found")
